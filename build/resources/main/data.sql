--
-- insert into orders (order_number) values ('Alice');
-- insert into orders (order_number) values ('Bob');

INSERT INTO USERS (username, password, enabled, first_name)
VALUES ('user', '$2a$10$RwgkWmviXdBg9HEZmnI6fuREQL04vzpQh9CtS0CgXw4bYe602ExFm', true, 'Alex');

INSERT INTO USERS (username, password, enabled, first_name)
VALUES ('admin', '$2a$10$F3g4Dc9rW5LRjgqLuOTizOuINcgxnuf9.cCP8jWvzsjb.2OZ19OG6', true, 'Bob');

INSERT INTO AUTHORITIES (username, authority)
VALUES ('user', 'ROLE_USER');

INSERT INTO AUTHORITIES (username, authority)
VALUES ('admin', 'ROLE_USER');
INSERT INTO AUTHORITIES (username, authority)
VALUES ('admin', 'ROLE_ADMIN');
