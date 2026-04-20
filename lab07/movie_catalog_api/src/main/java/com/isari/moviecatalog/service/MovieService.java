package com.isari.moviecatalog.service;

import com.isari.moviecatalog.domain.Actor;
import com.isari.moviecatalog.domain.Genre;
import com.isari.moviecatalog.domain.Movie;
import com.isari.moviecatalog.dto.ActorDto;
import com.isari.moviecatalog.dto.MovieDto;
import com.isari.moviecatalog.dto.MovieRequest;
import com.isari.moviecatalog.repository.ActorRepository;
import com.isari.moviecatalog.repository.GenreRepository;
import com.isari.moviecatalog.repository.MovieRepository;
import com.isari.moviecatalog.web.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;

    @Transactional(readOnly = true)
    public List<MovieDto> findAll() {
        return movieRepository.findAll().stream()
                .map(MovieService::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieDto findById(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Movie", id));
        return toDto(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieDto> findByGenreId(Integer genreId) {
        if (!genreRepository.existsById(genreId)) {
            throw NotFoundException.of("Genre", genreId);
        }
        return movieRepository.findByGenreId(genreId).stream()
                .map(MovieService::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovieDto> searchByTitle(String fragment) {
        return movieRepository.findByTitleContainingIgnoreCase(fragment).stream()
                .map(MovieService::toDto)
                .toList();
    }

    public MovieDto create(MovieRequest request) {
        Movie movie = new Movie();
        applyRequest(movie, request);
        Movie saved = movieRepository.save(movie);
        return toDto(saved);
    }

    public MovieDto update(Integer id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Movie", id));
        applyRequest(movie, request);
        return toDto(movie);
    }

    public void delete(Integer id) {
        if (!movieRepository.existsById(id)) {
            throw NotFoundException.of("Movie", id);
        }
        movieRepository.deleteById(id);
    }

    public MovieDto addActor(Integer movieId, Integer actorId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> NotFoundException.of("Movie", movieId));
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> NotFoundException.of("Actor", actorId));
        movie.getActors().add(actor);
        return toDto(movie);
    }

    public MovieDto removeActor(Integer movieId, Integer actorId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> NotFoundException.of("Movie", movieId));
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> NotFoundException.of("Actor", actorId));
        movie.getActors().remove(actor);
        return toDto(movie);
    }

    private void applyRequest(Movie movie, MovieRequest request) {
        movie.setTitle(request.title());
        movie.setReleaseDate(request.releaseDate());
        movie.setDuration(request.duration());
        movie.setScore(request.score());

        if (request.genreId() != null) {
            Genre genre = genreRepository.findById(request.genreId())
                    .orElseThrow(() -> NotFoundException.of("Genre", request.genreId()));
            movie.setGenre(genre);
        } else {
            movie.setGenre(null);
        }

        if (request.actorIds() != null) {
            Set<Actor> resolved = new LinkedHashSet<>();
            for (Integer actorId : request.actorIds()) {
                Actor actor = actorRepository.findById(actorId)
                        .orElseThrow(() -> NotFoundException.of("Actor", actorId));
                resolved.add(actor);
            }
            movie.setActors(resolved);
        }
    }

    static MovieDto toDto(Movie movie) {
        List<ActorDto> actorDtos = movie.getActors().stream()
                .sorted(Comparator.comparing(Actor::getName))
                .map(ActorService::toDto)
                .toList();

        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getDuration(),
                movie.getScore(),
                GenreService.toDto(movie.getGenre()),
                actorDtos
        );
    }
}