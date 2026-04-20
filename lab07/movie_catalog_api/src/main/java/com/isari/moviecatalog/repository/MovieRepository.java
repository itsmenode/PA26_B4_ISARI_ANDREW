package com.isari.moviecatalog.repository;

import com.isari.moviecatalog.domain.Movie;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Override
    @EntityGraph(attributePaths = {"genre", "actors"})
    List<Movie> findAll();

    @Override
    @EntityGraph(attributePaths = {"genre", "actors"})
    Optional<Movie> findById(Integer id);

    @EntityGraph(attributePaths = {"genre", "actors"})
    List<Movie> findByGenreId(Integer genreId);

    @EntityGraph(attributePaths = {"genre", "actors"})
    List<Movie> findByTitleContainingIgnoreCase(String fragment);
}
