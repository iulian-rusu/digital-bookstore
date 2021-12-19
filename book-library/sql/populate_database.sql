INSERT INTO books(isbn, title, publisher, publish_year, genre, price, stock)
VALUES ('1111-1111-111', 'Enigma Otiliei', 'RO', 1935, 'Drama', 100, 20),
       ('1111-1111-222', 'C++ Templates', 'CXX', 2004, 'Tech', 150, 30),
       ('1111-1111-333', 'The Lord Of The Rings', 'TK', 1986, 'Fantasy', 75, 10),
       ('1111-1111-444', 'The Silmarillion', 'TK', 1966, 'Fantasy', 85, 200),
       ('1111-1111-555', 'Lord of the Flies', 'WG', 2003, 'Fantasy', 88, 50),
       ('1111-1111-666', 'Ion', 'RO', 1993, 'Drama', 92, 90),
       ('1111-1111-777', 'Morometii', 'MP', 1955, 'Novel', 199, 40);

INSERT INTO authors(first_name, last_name)
VALUES ('John', 'Tolkien'),
       ('Marin', 'Preda'),
       ('Liviu', 'Rebreanu'),
       ('William', 'Shakespeare'),
       ('Nicolai', 'Josuttis'),
       ('Andrei', 'Alexandrescu'),
       ('Lewis', 'Caroll'),
       ('William', 'Golding');

INSERT INTO book_authors(isbn, author_index, author_id)
VALUES ('1111-1111-111', 1, (SELECT author_id from authors WHERE last_name = 'Shakespeare')),
       ('1111-1111-111', 2, (SELECT author_id from authors WHERE last_name = 'Caroll')),
       ('1111-1111-222', 1, (SELECT author_id from authors WHERE last_name = 'Alexandrescu')),
       ('1111-1111-333', 1, (SELECT author_id from authors WHERE last_name = 'Tolkien')),
       ('1111-1111-444', 1, (SELECT author_id from authors WHERE last_name = 'Tolkien')),
       ('1111-1111-555', 1, (SELECT author_id from authors WHERE last_name = 'Golding')),
       ('1111-1111-666', 1, (SELECT author_id from authors WHERE last_name = 'Rebreanu')),
       ('1111-1111-777', 1, (SELECT author_id from authors WHERE last_name = 'Preda'));


