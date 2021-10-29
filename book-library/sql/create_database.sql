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
    PRIMARY KEY (author_id),
    CONSTRAINT uk_author_name UNIQUE (first_name, last_name)
);

CREATE TABLE IF NOT EXISTS book_authors
(
    isbn         CHAR(13) NOT NULL,
    author_index INTEGER NOT NULL,
    author_id    INTEGER NOT NULL,
    CONSTRAINT pk_book_authors PRIMARY KEY (author_index, isbn),
    CONSTRAINT fk_isbn FOREIGN KEY (isbn) REFERENCES books (isbn) ON DELETE CASCADE,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES authors (author_id) ON DELETE CASCADE,
    CONSTRAINT uk_author_index UNIQUE (author_id, isbn)
);

CREATE INDEX idx_book_publish_year ON books (publish_year);
CREATE INDEX idx_book_genre ON books (genre);
CREATE INDEX idx_author_first_name ON authors (first_name);
CREATE INDEX idx_author_last_name ON authors (last_name);