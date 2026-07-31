DELETE FROM movie_actor;
DELETE FROM actor;
DELETE FROM movie;

INSERT INTO movie (id, name, genre, publication_date) VALUES (1, 'Inception', 'ACTION', '2010-07-16');
INSERT INTO movie (id, name, genre, publication_date) VALUES (2, 'Interstellar', 'HORROR', '2014-11-07');
INSERT INTO movie (id, name, genre, publication_date) VALUES (3, 'Oppenheimer', 'DRAMA', '2023-07-21');

INSERT INTO actor (id, first_name, last_name) VALUES (10, 'Leonardo', 'DiCaprio');
INSERT INTO actor (id, first_name, last_name) VALUES (11, 'Cillian', 'Murphy');
INSERT INTO actor (id, first_name, last_name) VALUES (12, 'Anne', 'Hathaway');

INSERT INTO movie_actor (movie_id, actor_id) VALUES (1, 10);
INSERT INTO movie_actor (movie_id, actor_id) VALUES (1, 11);
INSERT INTO movie_actor (movie_id, actor_id) VALUES (2, 12);
INSERT INTO movie_actor (movie_id, actor_id) VALUES (3, 11);

-- ALTER TABLE movie ALTER COLUMN id RESTART WITH 100;
-- ALTER TABLE actor ALTER COLUMN id RESTART WITH 100;
-- ALTER TABLE movie_actor ALTER COLUMN id RESTART WITH 100;