package com.rij.amethyst_dev.models.MD;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MDRepository extends JpaRepository<MD, Long> {
    @EntityGraph(attributePaths = { "tags" })
    MD findByPath(String path);

    @EntityGraph(attributePaths = { "tags" })
    @Query("SELECT md FROM md_storage md JOIN md.tags tag WHERE md.content LIKE %?1% AND tag.tag = 'wiki'")
    List<MD> findWikisThatMention(String input, Pageable pageable);



/*
    @EntityGraph(attributePaths = { "tags" })
    @Query(value = "SELECT md.id, md.imageUrl, md.groupName, md.title, md.orderPosition, md.path, md.tags FROM md_storage md WHERE md.tags = 'wiki'")
    List<Object[]> getWikiGroupes();
 */

    @EntityGraph(attributePaths = { "tags" })
    List<MD> getAllByTagsTag(String tag);
}
