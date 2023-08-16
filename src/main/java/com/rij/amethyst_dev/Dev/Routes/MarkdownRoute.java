package com.rij.amethyst_dev.Dev.Routes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.Dev.MarkdownProcessing.MD.MD;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.Helpers.HTMLStringProcessors;
import com.rij.amethyst_dev.Services.MDService;
import com.rij.amethyst_dev.jsons.MDDocument;
import com.rij.amethyst_dev.models.Userdb.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.function.Function;

@RestController
@RequestMapping("/api/v2/markdown")
public class MarkdownRoute {
    ResponseEntity UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    private final MDService mdService;
    private final Authorizator authorizator;

    public MarkdownRoute(MDService mdService, Authorizator authorizator) {
        this.mdService = mdService;
        this.authorizator = authorizator;
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


    @GetMapping("/get/**")
    public ResponseEntity<String> GetParsedMd(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String dynamicPath = path.substring("/api/v2/markdown/get/".length());

        MD md = mdService.getByPath(dynamicPath);

        if (md == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dynamicPath + " Not found");
        }

        if (md.getRenderedContent() == null) {
            md.setRenderedContent(mdService.Render(md.getContent()));
            mdService.save(md);
        }

        md.setContent(null);

        return mapjson.apply(md);
    }

    @PostMapping("/render")
    public ResponseEntity<String> testRender(@RequestBody MDDocument doc) {
        String rendered = mdService.Render(doc.getContent());
        return ResponseEntity.ok(rendered);
    }


    @GetMapping("/getAll")
    public ResponseEntity<String> getAll() {
        return mapjson.apply(mdService.getAll());
    }

    @GetMapping("/getSource")
    public ResponseEntity<Object> getSource(@RequestParam String param) {

        MD md = mdService.getByPath(param);

        if (md == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
        }

        return ResponseEntity.ok(md);
    }

    @PostMapping("/edit")
    public ResponseEntity<String> EditSource(@CookieValue(value = "_dt", defaultValue = "a") String token, @RequestBody MDDocument doc) {
        User user = authorizator.authorizedUser(token);
        if (user == null)
            return UNAUTHORIZED;

        MD md = mdService.getByPath(doc.getPath());

        if (md == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(doc.getContent() + " Not found");
        }

        md.setContent(doc.getContent());
        md.setRenderedContent(mdService.Render(doc.getContent()));
        mdService.save(md);
        return mapjson.apply(md);
    }


    @PostMapping("/add")
    public ResponseEntity<String> addDocument(@CookieValue(value = "_dt", defaultValue = "a") String token, @RequestBody MD md) {
        User user = authorizator.authorizedUser(token);
        if (user == null)
            return UNAUTHORIZED;

        if (md == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");

        if (md.getPath() == null || !HTMLStringProcessors.hasAtLeastOneNonSpaceCharacter(md.getPath()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");

        if (md.getContent() == null || !HTMLStringProcessors.hasAtLeastOneNonSpaceCharacter(md.getContent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");

        if (md.getTitle() == null || !HTMLStringProcessors.hasAtLeastOneNonSpaceCharacter(md.getContent()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");


        if (mdService.getByPath(md.getPath()) != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("already exists");


        md.getTags().forEach(mdTag -> mdTag.setMd(md));


        mdService.save(md);
        //mdService.addDocument(body.getPath(), body.getContent());

        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> DeleteDocument(@CookieValue(value = "_dt", defaultValue = "a") String token, @RequestParam String param) {
        User user = authorizator.authorizedUser(token);
        if (user == null)
            return UNAUTHORIZED;

        mdService.DeleteDocument(param);

        return ResponseEntity.ok("ok");
    }
}
