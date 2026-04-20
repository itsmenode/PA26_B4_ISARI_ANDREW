package com.isari.moviecatalog.dto;

import java.time.LocalDate;

public record MovieReportDto(
        Integer movieId,
        String title,
        LocalDate releaseDate,
        Integer duration,
        Double score,
        String genre,
        String actors
) {
}
