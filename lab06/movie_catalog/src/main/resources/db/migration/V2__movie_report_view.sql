CREATE OR REPLACE VIEW movie_report AS
SELECT
    m.id                                                       AS movie_id,
    m.title,
    m.release_date,
    m.duration,
    m.score,
    g.name                                                     AS genre,
    COALESCE(STRING_AGG(a.name, ', ' ORDER BY a.name), '')     AS actors
FROM movies m
         LEFT JOIN genres       g  ON m.genre_id = g.id
         LEFT JOIN movie_actors ma ON m.id       = ma.movie_id
         LEFT JOIN actors       a  ON ma.actor_id = a.id
GROUP BY m.id, m.title, m.release_date, m.duration, m.score, g.name
ORDER BY m.title;