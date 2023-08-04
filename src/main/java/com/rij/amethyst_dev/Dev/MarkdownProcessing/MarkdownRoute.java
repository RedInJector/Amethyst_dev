package com.rij.amethyst_dev.Dev.MarkdownProcessing;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.Dev.MarkdownProcessing.MD.MD;
import com.rij.amethyst_dev.Dev.MarkdownProcessing.MD.MDService;
import com.rij.amethyst_dev.Helpers.TimeTester;
import com.rij.amethyst_dev.jsons.MDDocument;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v2/markdown")
public class MarkdownRoute {



    private final MDService mdService;

    public MarkdownRoute(MDService mdService) {
        this.mdService = mdService;
    }

    private final Function<Object, ResponseEntity<String>> mapjson = obj -> {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            //TODO: logger.error(String.valueOf(e));
            return ResponseEntity.internalServerError().body("Internal Error");
        }
    };


    private String Render(String input) {
        TimeTester time1 = new TimeTester();
        time1.start("render");
        MutableDataSet options = new MutableDataSet()
                .set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), AttributesExtension.create()))
                .setFrom(ParserEmulationProfile.GITHUB)
                .set(Parser.LISTS_BULLET_ITEM_INTERRUPTS_PARAGRAPH, true)
                .set(AttributesExtension.ASSIGN_TEXT_ATTRIBUTES, true);


        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(input);
        time1.end();
        return renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
    }



    @GetMapping("/get/**")
    public ResponseEntity<String> GetParsedMd(HttpServletRequest request){
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String dynamicPath = path.substring("/api/v2/markdown/get/".length());

        MD md = mdService.getByPath(dynamicPath);

        if(md == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dynamicPath + " Not found");
        }

        if(md.getRenderedContent() == null) {
            md.setRenderedContent(Render(md.getContent()));
            mdService.save(md);
        }

        return ResponseEntity.ok(Render(md.getRenderedContent()));
    }

    @PostMapping("/render")
    public ResponseEntity<String> testRender(@RequestBody MDDocument doc){
        String rendered = Render(doc.getContent());
        return ResponseEntity.ok(rendered);
    }



    @GetMapping("/getAll")
    public ResponseEntity<String> getAll(){

        return mapjson.apply(mdService.getAll());
    }

    @GetMapping("/getSource")
    public ResponseEntity<String> getSource(@RequestParam String param){
        MD md = mdService.getByPath(param);

        if(md == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(param + " Not found");
        }


        return mapjson.apply(md);
    }

    @PostMapping("/edit")
    public ResponseEntity<String> EditSource(@RequestBody MDDocument doc){
        MD md = mdService.getByPath(doc.getPath());

        if(md == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(doc.getContent() + " Not found");
        }

        md.setContent(doc.getContent());
        md.setRenderedContent(Render(doc.getContent()));
        mdService.save(md);
        return mapjson.apply(md);
    }



    @PostMapping("/add")
    public ResponseEntity<String> addDocument(@RequestBody MDDocument body){

        if(body == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");

        if(body.getPath() == null || !hasAtLeastOneNonSpaceCharacter(body.getPath()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");

        if(body.getContent() == null || !hasAtLeastOneNonSpaceCharacter(body.getContent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");

        if(mdService.getByPath(body.getPath()) != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("already exists");


        mdService.addDocument(body.getPath(), body.getContent());

        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> DeleteDocument(){
        mdService.DeleteDocument("");

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/search")
    public ResponseEntity<String> findbyString(@RequestParam String param){
        String s = "";
        for(String s1 : mdService.search(param)){
            s = new StringBuilder(s).append(s1).toString();
        }

        return ResponseEntity.ok(s);
    }




    public static boolean hasAtLeastOneNonSpaceCharacter(String str) {
        return str != null && !str.trim().isEmpty();
    }
}