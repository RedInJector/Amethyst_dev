package com.rij.amethyst_dev.Routes;


import com.rij.amethyst_dev.models.MD.MD;
import com.rij.amethyst_dev.Helpers.HTMLStringProcessors;
import com.rij.amethyst_dev.Services.MDService;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/markdown/wiki")
public class WikiRoutes {
    private final MDService mdService;

    public WikiRoutes(MDService mdService) {
        this.mdService = mdService;
    }

    private String Render(String input) {
        MutableDataSet options = new MutableDataSet()
                .set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), AttributesExtension.create()))
                .setFrom(ParserEmulationProfile.GITHUB)
                .set(Parser.LISTS_BULLET_ITEM_INTERRUPTS_PARAGRAPH, true)
                .set(AttributesExtension.ASSIGN_TEXT_ATTRIBUTES, true);


        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(input);

        return renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
    }


    @GetMapping("/search")
    public ResponseEntity<List<MD>> findbyString(@RequestParam String param) {
        List<MD> res = mdService.searchInWiki(param)
                .stream()
                .peek(md -> {
                    if (md.getRenderedContent() == null)
                        md.setRenderedContent(Render(md.getContent()));

                    String removedTags = HTMLStringProcessors.removeHtmlTags(md.getRenderedContent());
                    String found = HTMLStringProcessors.extractTextAroundWord(removedTags, param);

                    md.setContent(found);
                    md.setRenderedContent(null);
                })
                .filter(md -> md.getContent() != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }


    @GetMapping("/getgroupes")
    public ResponseEntity<Object> getWikiGroupes() {
        List<MD> res = mdService.getWikiGroupes();

        return ResponseEntity.ok(res);
    }
}
