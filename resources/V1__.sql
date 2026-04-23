CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE users
(
    id       BIGINT       NOT NULL,
    surname  VARCHAR(255),
    name     VARCHAR(255),
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);
ALTER TABLE friend_requests
    DROP COLUMN created_at;

ALTER TABLE friend_requests
    ADD created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL;

ALTER TABLE journals
    ALTER COLUMN title DROP NOT NULL;

ALTER TABLE journals
    ALTER COLUMN visibility DROP NOT NULL;