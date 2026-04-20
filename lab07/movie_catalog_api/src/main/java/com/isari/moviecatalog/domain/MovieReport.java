package com.isari.moviecatalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.LocalDate;

@Entity
@Immutable
@Subselect("SELECT * FROM movie_report")
@Synchronize({"movies", "genres", "actors", "movie_actors"})
public class MovieReport {

    @Id
    @Column(name = "movie_id")
    private Integer movieId;

    private String title;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Integer duration;

    private Double score;

    private String genre;

    private String actors;

    public Integer getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public Double getScore() {
        return score;
    }

    public String getGenre() {
        return genre;
    }

    public String getActors() {
        return actors;
    }
}