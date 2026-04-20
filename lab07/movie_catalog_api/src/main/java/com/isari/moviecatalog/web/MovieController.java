package com.isari.moviecatalog.web;

import com.isari.moviecatalog.dto.MovieDto;
import com.isari.moviecatalog.dto.MovieRequest;
import com.isari.moviecatalog.dto.ScoreUpdateRequest;
import com.isari.moviecatalog.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "CRUD operations for movies")
public class MovieController {

    private final MovieService movieService;

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

    @PatchMapping("/{id}/score")
    public MovieDto updateScore(@PathVariable Integer id,
                                @Valid @RequestBody ScoreUpdateRequest request) {
        return movieService.updateScore(id, request.score());
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