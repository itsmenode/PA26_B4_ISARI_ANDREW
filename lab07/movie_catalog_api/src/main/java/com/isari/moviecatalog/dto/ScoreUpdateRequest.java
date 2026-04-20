package com.isari.moviecatalog.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ScoreUpdateRequest(
        @NotNull
        @DecimalMin(value = "0.0", message = "score must be between 0.0 and 10.0")
        @DecimalMax(value = "10.0", message = "score must be between 0.0 and 10.0")
        Double score
) {
}