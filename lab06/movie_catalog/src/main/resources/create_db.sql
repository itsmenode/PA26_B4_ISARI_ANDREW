DROP VIEW IF EXISTS movie_report CASCADE;
DROP TABLE IF EXISTS movie_actors CASCADE;
DROP TABLE IF EXISTS movies CASCADE;
DROP TABLE IF EXISTS actors CASCADE;
DROP TABLE IF EXISTS genres CASCADE;

CREATE TABLE genres (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE movies (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        release_date DATE,
                        duration INTEGER,
                        score NUMERIC(3, 1),
                        genre_id INTEGER REFERENCES genres(id)
);

CREATE TABLE actors (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

CREATE TABLE movie_actors (
                              movie_id INTEGER REFERENCES movies(id) ON DELETE CASCADE,
                              actor_id INTEGER REFERENCES actors(id) ON DELETE CASCADE,
                              PRIMARY KEY (movie_id, actor_id)
);

CREATE OR REPLACE VIEW movie_report AS
SELECT
    m.id            AS movie_id,
    m.title,
    m.release_date,
    m.duration,
    m.score,
    g.name          AS genre,
    COALESCE(
            STRING_AGG(a.name, ', ' ORDER BY a.name), ''
    )               AS actors
FROM movies m
         LEFT JOIN genres g       ON m.genre_id = g.id
         LEFT JOIN movie_actors ma ON m.id = ma.movie_id
         LEFT JOIN actors a       ON ma.actor_id = a.id
GROUP BY m.id, m.title, m.release_date, m.duration, m.score, g.name
ORDER BY m.title;

INSERT INTO genres (name) VALUES
                              ('Action'),
                              ('Drama'),
                              ('Comedy'),
                              ('Horror'),
                              ('Sci-Fi'),
                              ('Thriller');

INSERT INTO movies (title, release_date, duration, score, genre_id) VALUES
                                                                        ('Inception',        '2010-07-16', 148, 8.8, 5),
                                                                        ('The Godfather',    '1972-03-24', 175, 9.2, 2),
                                                                        ('Superbad',         '2007-08-17', 113, 7.6, 3),
                                                                        ('The Dark Knight',  '2008-07-18', 152, 9.0, 1),
                                                                        ('Interstellar',     '2014-11-07', 169, 8.6, 5),
                                                                        ('Pulp Fiction',     '1994-10-14', 154, 8.9, 2);

INSERT INTO actors (name) VALUES
                              ('Leonardo DiCaprio'),
                              ('Marlon Brando'),
                              ('Jonah Hill'),
                              ('Christian Bale'),
                              ('Matthew McConaughey'),
                              ('John Travolta'),
                              ('Samuel L. Jackson');

INSERT INTO movie_actors (movie_id, actor_id) VALUES
                                                  (1, 1),
                                                  (2, 2),
                                                  (3, 3),
                                                  (4, 4),
                                                  (5, 5),
                                                  (6, 6),
                                                  (6, 7);