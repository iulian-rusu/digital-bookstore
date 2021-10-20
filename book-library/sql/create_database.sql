CREATE TABLE IF NOT EXISTS books
(
    isbn         CHAR(13)     NOT NULL,
    title        VARCHAR(256) NOT NULL UNIQUE,
    publisher    VARCHAR(64)  NOT NULL,
    publish_year INTEGER      NOT NULL,
    genre        VARCHAR(64)  NOT NULL,
    PRIMARY KEY (isbn)
);

CREATE TABLE IF NOT EXISTS authors
(
    author_id  INTEGER     NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(64) NOT NULL,
    last_name  VARCHAR(64) NOT NULL,
    PRIMARY KEY (author_id)
);

CREATE TABLE IF NOT EXISTS book_authors
(
    isbn         CHAR(13) REFERENCES books (isbn),
    author_id    INTEGER REFERENCES authors (author_id),
    author_index INTEGER NOT NULL,
    CONSTRAINT pk_book_authors PRIMARY KEY (author_id, isbn),
    CONSTRAINT uk_author_index UNIQUE (author_index, isbn)
);

CREATE INDEX idx_book_publish_year ON books (publish_year);
CREATE INDEX idx_book_genre ON books (genre);
CREATE INDEX idx_author_first_name ON authors (first_name);
CREATE INDEX idx_author_last_name ON authors (last_name);