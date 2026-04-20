package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieList {

    private int id;
    private String name;
    private LocalDateTime createdAt;
    private List<Movie> movies = new ArrayList<>();

    public MovieList(String name) {
        this.name = name;
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