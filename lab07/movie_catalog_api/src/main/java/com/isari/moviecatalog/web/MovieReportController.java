package com.isari.moviecatalog.web;

import com.isari.moviecatalog.dto.MovieReportDto;
import com.isari.moviecatalog.service.MovieReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Denormalized movie report from the movie_report view")
public class MovieReportController {

    private final MovieReportService reportService;

    @GetMapping("/movies")
    public List<MovieReportDto> movies() {
        return reportService.findAll();
    }
}