import algo.UnrelatedMoviesPartitioner;
import dao.MovieDAO;
import dao.MovieListDAO;
import db.Database;
import db.MigrationRunner;
import io.CsvImporter;
import model.Movie;
import model.MovieList;
import report.ReportGenerator;

import java.io.File;
import java.net.URL;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("=== Flyway migrate ===");
            new MigrationRunner().migrate();

            System.out.println("\n=== CSV import ===");
            URL csvDir = Main.class.getClassLoader().getResource("sample_data");
            if (csvDir != null) {
                File dir = new File(csvDir.toURI());
                CsvImporter importer = new CsvImporter();
                CsvImporter.ImportStats stats = importer.importFromDirectory(dir.getAbsolutePath());
                System.out.println("Imported: " + stats);
            } else {
                System.out.println("sample_data/ not on classpath – skipping CSV import");
            }

            System.out.println("\n=== Partition into unrelated-movie lists ===");
            MovieDAO movieDAO = new MovieDAO();
            List<Movie> allMovies = movieDAO.findAllWithActors();
            System.out.println("Total movies in DB: " + allMovies.size());

            UnrelatedMoviesPartitioner partitioner = new UnrelatedMoviesPartitioner();
            List<List<Movie>> partitions = partitioner.partition(allMovies);
            System.out.println("Number of lists (as small as possible): " + partitions.size());

            MovieListDAO movieListDAO = new MovieListDAO();
            for (int i = 0; i < partitions.size(); i++) {
                List<Movie> bucket = partitions.get(i);
                MovieList stored = movieListDAO.create("Unrelated Set #" + (i + 1));
                for (Movie m : bucket) {
                    movieListDAO.addMovieToList(stored.getId(), m.getId());
                }
                System.out.println("  List " + (i + 1) + " (size " + bucket.size() + "): "
                        + bucket.stream().map(Movie::getTitle).toList());
            }

            System.out.println("\n=== Stored movie lists ===");
            for (MovieList list : movieListDAO.findAll()) {
                System.out.println("  " + list);
            }

            System.out.println("\n=== HTML Report ===");
            new ReportGenerator().generate("movie_report.html");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Database.getInstance().closePool();
        }
    }
}
