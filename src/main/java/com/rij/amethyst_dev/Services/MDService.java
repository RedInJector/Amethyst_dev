package com.rij.amethyst_dev.Services;


import com.rij.amethyst_dev.MarkdownProcessing.MD.MD;
import com.rij.amethyst_dev.MarkdownProcessing.MD.MDRepository;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MDService {


    private final MDRepository mdRepository;


    public MDService(MDRepository mdRepository) {
        this.mdRepository = mdRepository;
    }


    public MD getByPath(String path) {
        return mdRepository.findByPath(path);
    }

    public List<MD> getAll() {
        return mdRepository.findAll();
    }

    public void DeleteDocument(String path) {
        MD md = mdRepository.findByPath(path);
        mdRepository.delete(md);
    }

    public void save(MD md) {
        mdRepository.save(md);
    }


    public List<MD> searchInWiki(String input) {
        Pageable pageable = PageRequest.of(0, 5);
        List<MD> mds = mdRepository.findWikisThatMention(input, pageable);


        return mds;
    }


    public List<MD> getWikiGroupes(){
        List<MD> res = mdRepository.getAllByTagsTag("wiki");

        return res;
    }


    public String Render(String rewMarkdown) {
        MutableDataSet options = new MutableDataSet()
                .set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), AttributesExtension.create()))
                .setFrom(ParserEmulationProfile.GITHUB)
                .set(Parser.LISTS_BULLET_ITEM_INTERRUPTS_PARAGRAPH, true)
                .set(AttributesExtension.ASSIGN_TEXT_ATTRIBUTES, true);


        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(rewMarkdown);

        return renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
    }

}
