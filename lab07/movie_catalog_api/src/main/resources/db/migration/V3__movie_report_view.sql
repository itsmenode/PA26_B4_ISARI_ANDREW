CREATE
OR REPLACE VIEW movie_report AS
SELECT m.id                                                   AS movie_id,
       m.title,
       m.release_date,
       m.duration,
       m.score,
       g.name                                                 AS genre,
       COALESCE(STRING_AGG(a.name, ', ' ORDER BY a.name), '') AS actors
FROM movies m
         LEFT JOIN genres g ON g.id = m.genre_id
         LEFT JOIN movie_actors ma ON ma.movie_id = m.id
         LEFT JOIN actors a ON a.id = ma.actor_id
GROUP BY m.id, g.name;