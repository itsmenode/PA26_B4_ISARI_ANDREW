package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieList {

    private int id;
    private String name;
    private LocalDateTime createdAt;
    private List<Movie> movies = new ArrayList<>();

    public MovieList() {
    }

    public MovieList(int id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public MovieList(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "MovieList{id=" + id
                + ", name='" + name + "'"
                + ", createdAt=" + createdAt
                + ", size=" + movies.size()
                + "}";
    }
}