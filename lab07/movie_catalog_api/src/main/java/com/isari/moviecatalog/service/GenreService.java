package com.isari.moviecatalog.service;

import com.isari.moviecatalog.domain.Genre;
import com.isari.moviecatalog.dto.GenreDto;
import com.isari.moviecatalog.dto.GenreRequest;
import com.isari.moviecatalog.repository.GenreRepository;
import com.isari.moviecatalog.web.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(GenreService::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GenreDto findById(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Genre", id));
        return toDto(genre);
    }

    public GenreDto create(GenreRequest request) {
        Genre genre = new Genre(request.name());
        Genre saved = genreRepository.save(genre);
        return toDto(saved);
    }

    public GenreDto update(Integer id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Genre", id));
        genre.setName(request.name());
        return toDto(genre);
    }

    public void delete(Integer id) {
        if (!genreRepository.existsById(id)) {
            throw NotFoundException.of("Genre", id);
        }
        genreRepository.deleteById(id);
    }

    static GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreDto(genre.getId(), genre.getName());
    }
}