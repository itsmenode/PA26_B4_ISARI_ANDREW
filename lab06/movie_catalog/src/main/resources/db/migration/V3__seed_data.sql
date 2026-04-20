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
