CREATE TABLE IF NOT EXISTS users
(
    user_id       INTEGER       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(64)   NOT NULL,
    password_hash VARBINARY(64) NOT NULL,
    role          CHAR(16)      NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uk_users_username UNIQUE (username)
);

ALTER TABLE users
    ADD CONSTRAINT ck_users_username CHECK (username REGEXP '^[_a-zA-Z]\\w{2,}$');