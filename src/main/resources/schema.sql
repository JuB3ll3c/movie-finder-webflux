CREATE TABLE movie(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    genre VARCHAR(20),
    publication_date DATE
);

CREATE TABLE actor(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255)
);

CREATE TABLE movie_actor(
    movie_id BIGINT,
    actor_id BIGINT,
    PRIMARY KEY (movie_id, actor_id)
);