ALTER TABLE user
    ADD role VARCHAR(255);

UPDATE user
SET role = CASE WHEN admin = true THEN 'ADMIN' ELSE 'PLAYER' END;

ALTER TABLE user
DROP COLUMN admin;