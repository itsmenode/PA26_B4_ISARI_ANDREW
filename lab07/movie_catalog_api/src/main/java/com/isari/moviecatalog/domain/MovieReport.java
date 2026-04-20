package com.isari.moviecatalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.LocalDate;

@Entity
@Immutable
@Subselect("SELECT * FROM movie_report")
@Synchronize({"movies", "genres", "actors", "movie_actors"})
@Getter
@NoArgsConstructor
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
}