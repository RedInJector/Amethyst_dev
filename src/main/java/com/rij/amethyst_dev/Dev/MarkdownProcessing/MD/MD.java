package com.rij.amethyst_dev.Dev.MarkdownProcessing.MD;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Entity(name = "md_storage")
public class MD {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "path")
    private String path;
    @Column(name = "content")
    private String content;
    @Column(name = "title")
    private String title;
    @Column(name = "rendered_content")
    private String renderedContent;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "is_wiki")
    private boolean isWiki;
    @Column(name = "order_position")
    private Integer orderPosition;
    @Column(name = "group_name ")
    private String groupName;

    public MD(){}
}
