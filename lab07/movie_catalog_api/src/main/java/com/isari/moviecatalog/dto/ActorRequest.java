package com.isari.moviecatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActorRequest(
        @NotBlank
        @Size(max = 255)
        String name
) {
}
