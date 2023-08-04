package com.rij.amethyst_dev.Dev.MarkdownProcessing.MD;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MDRepository extends JpaRepository<MD, Long> {
    MD findByPath(String path);

    @Query(value = "SELECT * FROM md_storage md WHERE MATCH(md.content) AGAINST(?1)", nativeQuery = true)
    List<MD> findOnesThatMension(String input, Pageable pageable);
}
