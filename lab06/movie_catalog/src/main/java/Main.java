import dao.*;
import db.Database;
import model.*;
import report.ReportGenerator;

import java.time.LocalDate;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("=== GenreDAO ===");
            GenreDAO genreDAO = new GenreDAO();

            Genre western = genreDAO.create("Western");
            System.out.println("Created:              " + western);

            Genre action = genreDAO.findById(1);
            System.out.println("findById(1):          " + action);

            Genre drama = genreDAO.findByName("Drama");
            System.out.println("findByName(Drama):    " + drama);

            List<Genre> allGenres = genreDAO.findAll();
            System.out.println("All genres:           " + allGenres);

            System.out.println("\n=== ActorDAO ===");
            ActorDAO actorDAO = new ActorDAO();

            Actor newActor = actorDAO.create("Clint Eastwood");
            System.out.println("Created:              " + newActor);

            Actor found = actorDAO.findById(1);
            System.out.println("findById(1):          " + found);

            Actor byName = actorDAO.findByName("Marlon Brando");
            System.out.println("findByName(Brando):   " + byName);

            System.out.println("\n=== MovieDAO ===");
            MovieDAO movieDAO = new MovieDAO();

            Movie newMovie = new Movie("The Good, the Bad and the Ugly",
                    LocalDate.of(1966, 12, 23), 178, 8.8);
            newMovie.setGenre(western);
            movieDAO.create(newMovie);
            System.out.println("Created:              " + newMovie);

            Movie movieById = movieDAO.findById(1);
            System.out.println("findById(1):          " + movieById);

            List<Movie> sciFiMovies = movieDAO.findByGenre(5);
            System.out.println("Sci-Fi movies:        " + sciFiMovies);

            System.out.println("\n=== MovieActorDAO ===");
            MovieActorDAO movieActorDAO = new MovieActorDAO();

            movieActorDAO.addActorToMovie(newMovie.getId(), newActor.getId());
            System.out.println("Linked actor " + newActor.getId()
                    + " to movie " + newMovie.getId());

            List<Actor> castOfNew = movieActorDAO.findActorsByMovie(newMovie.getId());
            System.out.println("Cast of new movie:    " + castOfNew);

            List<Actor> castPF = movieActorDAO.findActorsByMovie(6);
            System.out.println("Cast of Pulp Fiction: " + castPF);

            System.out.println("\n=== HTML Report ===");
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generate("movie_report.html");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Database.getInstance().closePool();
        }
    }
}