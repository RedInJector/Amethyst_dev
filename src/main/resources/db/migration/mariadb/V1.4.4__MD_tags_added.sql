CREATE TABLE md_tags
(
    id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag   VARCHAR(255),
    md_id BIGINT,
    FOREIGN KEY (md_id) REFERENCES md_storage (id)
);

ALTER TABLE md_storage
    DROP COLUMN is_wiki;


