CREATE TABLE files
(
    id        BIGSERIAL PRIMARY KEY,
    filename  VARCHAR(255),
    size      BIGINT,
    user_id   BIGINT,
    file_path VARCHAR(500),

    CONSTRAINT fk_files_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);