package com.isari.movieclient.dto;

import java.time.LocalDate;
import java.util.List;

public record MovieDto(
        Integer id,
        String title,
        LocalDate releaseDate,
        Integer duration,
        Double score,
        GenreDto genre,
        List<ActorDto> actors
) {
}
