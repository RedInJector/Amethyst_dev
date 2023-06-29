package com.rij.amethyst_dev.jsons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class addComment {
    @JsonProperty("Author")
    private String Author_Token;
    @JsonProperty("Content")
    private String content;
    @JsonProperty("PostID")
    private String postid;

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

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
