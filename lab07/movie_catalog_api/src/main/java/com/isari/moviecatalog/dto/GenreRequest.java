package com.isari.moviecatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequest(
        @NotBlank
        @Size(max = 100)
        String name
) {
}
