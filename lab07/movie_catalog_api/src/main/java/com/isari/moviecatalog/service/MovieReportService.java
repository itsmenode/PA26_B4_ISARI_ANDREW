package com.isari.moviecatalog.service;

import com.isari.moviecatalog.domain.MovieReport;
import com.isari.moviecatalog.dto.MovieReportDto;
import com.isari.moviecatalog.repository.MovieReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MovieReportService {

    private final MovieReportRepository reportRepository;

    public MovieReportService(MovieReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<MovieReportDto> findAll() {
        return reportRepository.findAll().stream()
                .map(MovieReportService::toDto)
                .toList();
    }

    private static MovieReportDto toDto(MovieReport report) {
        return new MovieReportDto(
                report.getMovieId(),
                report.getTitle(),
                report.getReleaseDate(),
                report.getDuration(),
                report.getScore(),
                report.getGenre(),
                report.getActors()
        );
    }
}
