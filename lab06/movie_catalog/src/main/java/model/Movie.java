package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Movie {

    private int id;
    private String title;
    private LocalDate releaseDate;
    private int duration;
    private double score;
    private Genre genre;
    private List<Actor> actors = new ArrayList<>();

    public Movie() {
    }

    public Movie(String title, LocalDate releaseDate, int duration, double score) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.score = score;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    @Override
    public String toString() {
        return "Movie{id=" + id
                + ", title='" + title + "'"
                + ", releaseDate=" + releaseDate
                + ", duration=" + duration
                + ", score=" + score
                + ", genre=" + (genre != null ? genre.getName() : "null")
                + ", actors=" + actors
                + "}";
    }
}