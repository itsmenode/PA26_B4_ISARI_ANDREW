package com.isari.movieclient;

import com.isari.movieclient.dto.MovieDto;
import com.isari.movieclient.dto.MovieRequest;
import com.isari.movieclient.dto.ScoreUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiClientRunner implements CommandLineRunner {

    private final RestClient restClient;

    @Override
    public void run(String... args) {
        log.info("=== Listing all movies ===");
        List<MovieDto> movies = restClient.get()
                .uri("/api/movies")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        movies.forEach(m -> log.info("  #{} {} ({}), score={}",
                m.id(),
                m.title(),
                m.genre() != null ? m.genre().name() : "-",
                m.score()));

        log.info("=== POST: create a new movie ===");
        MovieRequest newMovie = new MovieRequest(
                "Client Test Movie",
                LocalDate.of(2024, 1, 1),
                100,
                7.0,
                1,
                List.of(1)
        );
        MovieDto created = restClient.post()
                .uri("/api/movies")
                .body(newMovie)
                .retrieve()
                .body(MovieDto.class);
        log.info("  created: id={}, title={}", created.id(), created.title());

        Integer id = created.id();

        log.info("=== PUT: replace the movie ===");
        MovieRequest replacement = new MovieRequest(
                "Client Test Movie (Updated)",
                LocalDate.of(2024, 6, 1),
                110,
                7.5,
                1,
                List.of(1, 2)
        );
        MovieDto updated = restClient.put()
                .uri("/api/movies/{id}", id)
                .body(replacement)
                .retrieve()
                .body(MovieDto.class);
        log.info("  updated: title={}, duration={}, actors={}",
                updated.title(), updated.duration(), updated.actors().size());

        log.info("=== PATCH: change only the score ===");
        MovieDto patched = restClient.patch()
                .uri("/api/movies/{id}/score", id)
                .body(new ScoreUpdateRequest(9.1))
                .retrieve()
                .body(MovieDto.class);
        log.info("  patched: score={}", patched.score());

        log.info("=== GET: fetch by id ===");
        MovieDto fetched = restClient.get()
                .uri("/api/movies/{id}", id)
                .retrieve()
                .body(MovieDto.class);
        log.info("  fetched: {} score={}", fetched.title(), fetched.score());

        log.info("=== DELETE: remove the movie ===");
        restClient.delete()
                .uri("/api/movies/{id}", id)
                .retrieve()
                .toBodilessEntity();
        log.info("  deleted id={}", id);
    }
}
