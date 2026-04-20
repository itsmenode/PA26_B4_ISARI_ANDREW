package com.isari.moviecatalog.web;

import com.isari.moviecatalog.dto.GenreDto;
import com.isari.moviecatalog.dto.GenreRequest;
import com.isari.moviecatalog.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<GenreDto> list() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public GenreDto getOne(@PathVariable Integer id) {
        return genreService.findById(id);
    }

    @PostMapping
    public ResponseEntity<GenreDto> create(@Valid @RequestBody GenreRequest request) {
        GenreDto created = genreService.create(request);
        return ResponseEntity
                .created(URI.create("/api/genres/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public GenreDto update(@PathVariable Integer id, @Valid @RequestBody GenreRequest request) {
        return genreService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        genreService.delete(id);
    }
}