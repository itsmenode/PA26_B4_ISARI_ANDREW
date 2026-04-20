package io;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dao.ActorDAO;
import dao.GenreDAO;
import dao.MovieActorDAO;
import dao.MovieDAO;
import model.Actor;
import model.Genre;
import model.Movie;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Imports movies, actors and cast relationships from a simple CSV dataset.
 *
 * Expected layout (one folder, three files):
 *   movies.csv        columns: title,release_date,duration,score,genre
 *   actors.csv        columns: name
 *   movie_actors.csv  columns: movie_title,actor_name
 *
 * release_date must be in ISO-8601 format (YYYY-MM-DD). A header row is expected.
 * Genres and actors are created on-demand (find-or-create by name).
 */
public class CsvImporter {

    private final GenreDAO genreDAO = new GenreDAO();
    private final ActorDAO actorDAO = new ActorDAO();
    private final MovieDAO movieDAO = new MovieDAO();
    private final MovieActorDAO movieActorDAO = new MovieActorDAO();

    public ImportStats importFromDirectory(String directory)
            throws IOException, CsvValidationException, java.sql.SQLException {
        ImportStats stats = new ImportStats();

        Map<String, Integer> movieIds = new HashMap<>();
        Map<String, Integer> actorIds = new HashMap<>();

        try (Reader reader = new FileReader(directory + "/movies.csv");
             CSVReader csv = new CSVReader(reader)) {
            csv.readNext();
            String[] row;
            while ((row = csv.readNext()) != null) {
                if (row.length < 5) continue;
                String title = row[0].trim();
                LocalDate releaseDate = parseDate(row[1]);
                int duration = parseInt(row[2], 0);
                double score = parseDouble(row[3], 0.0);
                String genreName = row[4].trim();

                Genre genre = genreDAO.create(genreName);
                Movie movie = new Movie(title, releaseDate, duration, score);
                movie.setGenre(genre);
                movieDAO.create(movie);

                movieIds.put(title, movie.getId());
                stats.moviesImported++;
            }
        }

        try (Reader reader = new FileReader(directory + "/actors.csv");
             CSVReader csv = new CSVReader(reader)) {
            csv.readNext();
            String[] row;
            while ((row = csv.readNext()) != null) {
                if (row.length < 1) continue;
                String name = row[0].trim();
                if (name.isEmpty()) continue;
                Actor actor = actorDAO.create(name);
                actorIds.put(name, actor.getId());
                stats.actorsImported++;
            }
        }

        try (Reader reader = new FileReader(directory + "/movie_actors.csv");
             CSVReader csv = new CSVReader(reader)) {
            csv.readNext();
            String[] row;
            while ((row = csv.readNext()) != null) {
                if (row.length < 2) continue;
                String title = row[0].trim();
                String actorName = row[1].trim();

                Integer movieId = movieIds.get(title);
                Integer actorId = actorIds.get(actorName);
                if (movieId == null || actorId == null) continue;

                movieActorDAO.addActorToMovie(movieId, actorId);
                stats.linksImported++;
            }
        }

        return stats;
    }

    private LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private double parseDouble(String s, double fallback) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    public static class ImportStats {
        public int moviesImported;
        public int actorsImported;
        public int linksImported;

        @Override
        public String toString() {
            return "ImportStats{movies=" + moviesImported
                    + ", actors=" + actorsImported
                    + ", links=" + linksImported + "}";
        }
    }
}
