package com.rij.amethyst_dev.jsons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePost {
    @JsonProperty("Author")
    private String Author_Token;
    @JsonProperty("Content")
    private String content;

    public String getAuthor_Token() {
        return Author_Token;
    }

    public void setAuthor_Token(String author_Token) {
        Author_Token = author_Token;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
