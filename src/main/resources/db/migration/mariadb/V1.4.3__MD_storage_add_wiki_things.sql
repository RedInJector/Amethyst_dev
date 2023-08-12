ALTER TABLE md_storage
ADD is_wiki bit(1) DEFAULT 0,
ADD order_position int DEFAULT NULL,
ADD group_name varchar(255) DEFAULT NULL