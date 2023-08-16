package com.rij.amethyst_dev.Dev.MarkdownProcessing.MD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(name = "md_tags")
public class MDTag {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tag")
    private String tag;
    @ManyToOne
    @JoinColumn(name = "md_id")
    @JsonIgnore
    private MD md;


    public MDTag(){}
}
