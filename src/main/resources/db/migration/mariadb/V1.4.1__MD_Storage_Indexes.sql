ALTER TABLE md_storage ADD FULLTEXT INDEX idx_ft (content);
ALTER TABLE md_storage ADD INDEX idx_c3 (content);