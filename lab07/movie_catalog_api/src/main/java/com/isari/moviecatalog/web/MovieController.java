package com.isari.moviecatalog.web;

import com.isari.moviecatalog.dto.MovieDto;
import com.isari.moviecatalog.dto.MovieRequest;
import com.isari.moviecatalog.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDto> list(@RequestParam(required = false) Integer genreId,
                               @RequestParam(required = false) String title) {
        if (genreId != null) {
            return movieService.findByGenreId(genreId);
        }
        if (title != null && !title.isBlank()) {
            return movieService.searchByTitle(title);
        }
        return movieService.findAll();
    }

    @GetMapping("/{id}")
    public MovieDto getOne(@PathVariable Integer id) {
        return movieService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MovieDto> create(@Valid @RequestBody MovieRequest request) {
        MovieDto created = movieService.create(request);
        return ResponseEntity
                .created(URI.create("/api/movies/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public MovieDto update(@PathVariable Integer id, @Valid @RequestBody MovieRequest request) {
        return movieService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        movieService.delete(id);
    }

    @PostMapping("/{movieId}/actors/{actorId}")
    public MovieDto addActor(@PathVariable Integer movieId, @PathVariable Integer actorId) {
        return movieService.addActor(movieId, actorId);
    }

    @DeleteMapping("/{movieId}/actors/{actorId}")
    public MovieDto removeActor(@PathVariable Integer movieId, @PathVariable Integer actorId) {
        return movieService.removeActor(movieId, actorId);
    }
}
