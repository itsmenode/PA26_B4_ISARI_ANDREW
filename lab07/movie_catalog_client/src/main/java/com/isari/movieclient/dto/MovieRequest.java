package com.isari.movieclient.dto;

import java.time.LocalDate;
import java.util.List;

public record MovieRequest(
        String title,
        LocalDate releaseDate,
        Integer duration,
        Double score,
        Integer genreId,
        List<Integer> actorIds
) {
}
