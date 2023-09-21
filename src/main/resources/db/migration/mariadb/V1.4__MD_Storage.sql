create table if not exists md_storage
(
    id bigint
        PRIMARY KEY AUTO_INCREMENT,
    path VARCHAR(255) UNIQUE NOT NULL,
    content TEXT
)