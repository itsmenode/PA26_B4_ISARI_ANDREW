package com.isari.moviecatalog.web;

import com.isari.moviecatalog.dto.GenreDto;
import com.isari.moviecatalog.dto.GenreRequest;
import com.isari.moviecatalog.service.GenreService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

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
