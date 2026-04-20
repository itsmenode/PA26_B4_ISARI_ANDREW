package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MovieReport {

    private int movieId;
    private String title;
    private LocalDate releaseDate;
    private int duration;
    private double score;
    private String genre;
    private String actors;
}