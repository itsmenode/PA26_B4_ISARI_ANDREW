package com.isari.moviecatalog.repository;

import com.isari.moviecatalog.domain.MovieReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieReportRepository extends JpaRepository<MovieReport, Integer> {
}
