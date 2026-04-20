package com.isari.moviecatalog.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record MovieRequest(
        @NotBlank
        @Size(max = 255)
        String title,

        LocalDate releaseDate,

        @Min(value = 1, message = "duration must be at least 1 minute")
        Integer duration,

        @DecimalMin(value = "0.0", message = "score must be between 0.0 and 10.0")
        @DecimalMax(value = "10.0", message = "score must be between 0.0 and 10.0")
        Double score,

        Integer genreId,

        List<Integer> actorIds
) {
}
