import algorithm.MoviePartitioner;
import dao.*;
import db.Database;
import importer.CsvImporter;
import model.*;
import report.ReportGenerator;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {

            Database.getInstance();

            System.out.println("\n=== CSV Import ===");
            long existingMovies = countMovies();
            if (existingMovies == 0) {
                CsvImporter importer = new CsvImporter();

                importer.importCsv("/data/sample_movies.csv");
            } else {
                System.out.println("Movies already loaded (" + existingMovies
                        + "). Skipping CSV import. Clear the tables or drop the DB to reimport.");
            }

            System.out.println("\n=== GenreDAO ===");
            GenreDAO genreDAO = new GenreDAO();
            List<Genre> genres = genreDAO.findAll();
            System.out.println("All genres: " + genres);

            System.out.println("\n=== ActorDAO ===");
            ActorDAO actorDAO = new ActorDAO();
            Actor dicaprio = actorDAO.findByName("Leonardo DiCaprio");
            System.out.println("findByName(Leonardo DiCaprio): " + dicaprio);

            System.out.println("\n=== MovieDAO ===");
            MovieDAO movieDAO = new MovieDAO();
            List<Movie> allMovies = movieDAO.findAll();
            System.out.println("Total movies: " + allMovies.size());
            allMovies.stream().limit(5).forEach(m -> System.out.println("  " + m.getTitle()));
            if (allMovies.size() > 5) {
                System.out.println("  ... and " + (allMovies.size() - 5) + " more.");
            }

            System.out.println("\n=== MovieActorDAO ===");
            MovieActorDAO movieActorDAO = new MovieActorDAO();
            if (!allMovies.isEmpty()) {
                Movie first = allMovies.get(0);
                List<Actor> cast = movieActorDAO.findActorsByMovie(first.getId());
                System.out.println("Cast of \"" + first.getTitle() + "\": " + cast);
            }

            System.out.println("\n=== Movie Partitioning (graph coloring) ===");
            MoviePartitioner partitioner = new MoviePartitioner();
            MoviePartitioner.Result result = partitioner.partition();
            System.out.println("Stats: " + result.stats);
            for (MovieList list : result.lists) {
                System.out.println(list.getName()
                        + " [" + list.getMovies().size() + " movies, created "
                        + list.getCreatedAt() + "]:");
                list.getMovies().forEach(m -> System.out.println("    " + m.getTitle()));
            }

            System.out.println("\n=== HTML Report ===");
            ReportGenerator reportGen = new ReportGenerator();
            reportGen.generate("movie_report.html");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Database.getInstance().closePool();
        }
    }

    private static long countMovies() throws java.sql.SQLException {
        try (java.sql.Connection c = Database.getInstance().getConnection();
             java.sql.Statement s = c.createStatement();
             java.sql.ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM movies")) {
            rs.next();
            return rs.getLong(1);
        }
    }
}