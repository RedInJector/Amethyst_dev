package com.rij.amethyst_dev.Dev.MarkdownProcessing.MD;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MDRepository extends JpaRepository<MD, Long> {
    MD findByPath(String path);

    @Query(value = "SELECT md FROM md_storage md WHERE md.content LIKE %?1% AND md.isWiki = true")
    List<MD> findWikisThatMention(String input, Pageable pageable);

    @Query(value = "SELECT md.id, md.imageUrl, md.groupName, md.title, md.orderPosition, md.path, md.isWiki FROM md_storage md WHERE md.isWiki = true")
    List<Object[]> getWikiGroupes();
}
