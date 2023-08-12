package com.rij.amethyst_dev.jsons;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MDDocument {
    private String path;
    private String content;
    private String imageUrl;
    private String title;
    private boolean wiki;
}
