package com.rij.amethyst_dev.Services;


import com.rij.amethyst_dev.Dev.MarkdownProcessing.MD.MD;
import com.rij.amethyst_dev.Dev.MarkdownProcessing.MD.MDRepository;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public List<MD> search(String input) {
        Pageable pageable = PageRequest.of(0, 5);
        List<MD> mds = mdRepository.findOnesThatMension(input, pageable);


        /*
        mds.forEach(md -> {
            results.addAll(searchMarkdownForString(md.getContent(), input));
        });
*/
        // Use regex to find the target string within the HTML


        return mds;
    }

    public static List<String> searchMarkdownForString(String markdownContent, String searchString) {
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.GITHUB);

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        List<String> paragraphsWithSearchString = new ArrayList<>();

        String cleanedMarkdownContent = markdownContent.replaceAll("<[^>]+>", "");

        // Split the markdown content into paragraphs using two consecutive newlines as the delimiter
        String[] paragraphs = cleanedMarkdownContent.split("\\n\\n");

        // Create a regex pattern to search for the given searchString in each paragraph
        String regex = "(?i)" + Pattern.quote(searchString); // Case-insensitive search
        Pattern pattern = Pattern.compile(regex);

        // Iterate through each paragraph and check if it contains the searchString
        for (String paragraph : paragraphs) {
            Matcher matcher = pattern.matcher(paragraph);
            if (matcher.find()) {
                Node document = parser.parse(paragraph);
                String htmlString = renderer.render(document);
                paragraphsWithSearchString.add(paragraph);
            }
        }

        return paragraphsWithSearchString;
    }


    public void addDocument(String path, String content) {
        MD md = new MD();
        md.setContent(content);
        md.setPath(path);

        mdRepository.save(md);
    }

}
