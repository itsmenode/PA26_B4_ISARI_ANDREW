package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Movie {

    private int id;
    private String title;
    private LocalDate releaseDate;
    private int duration;
    private double score;
    private Genre genre;
    private List<Actor> actors = new ArrayList<>();

    public Movie(String title, LocalDate releaseDate, int duration, double score) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.score = score;
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