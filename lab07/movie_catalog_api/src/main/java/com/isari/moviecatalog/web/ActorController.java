package com.isari.moviecatalog.web;

import com.isari.moviecatalog.dto.ActorDto;
import com.isari.moviecatalog.dto.ActorRequest;
import com.isari.moviecatalog.service.ActorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
@Tag(name = "Actors", description = "CRUD operations for actors")
public class ActorController {

    private final ActorService actorService;

    @GetMapping
    public List<ActorDto> list() {
        return actorService.findAll();
    }

    @GetMapping("/{id}")
    public ActorDto getOne(@PathVariable Integer id) {
        return actorService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ActorDto> create(@Valid @RequestBody ActorRequest request) {
        ActorDto created = actorService.create(request);
        return ResponseEntity
                .created(URI.create("/api/actors/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ActorDto update(@PathVariable Integer id, @Valid @RequestBody ActorRequest request) {
        return actorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        actorService.delete(id);
    }
}