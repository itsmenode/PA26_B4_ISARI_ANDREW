package com.isari.moviecatalog.service;

import com.isari.moviecatalog.domain.Actor;
import com.isari.moviecatalog.dto.ActorDto;
import com.isari.moviecatalog.dto.ActorRequest;
import com.isari.moviecatalog.repository.ActorRepository;
import com.isari.moviecatalog.web.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    @Transactional(readOnly = true)
    public List<ActorDto> findAll() {
        return actorRepository.findAll().stream()
                .map(ActorService::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ActorDto findById(Integer id) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Actor", id));
        return toDto(actor);
    }

    public ActorDto create(ActorRequest request) {
        Actor actor = new Actor(request.name());
        Actor saved = actorRepository.save(actor);
        return toDto(saved);
    }

    public ActorDto update(Integer id, ActorRequest request) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Actor", id));
        actor.setName(request.name());
        return toDto(actor);
    }

    public void delete(Integer id) {
        if (!actorRepository.existsById(id)) {
            throw NotFoundException.of("Actor", id);
        }
        actorRepository.deleteById(id);
    }

    static ActorDto toDto(Actor actor) {
        if (actor == null) {
            return null;
        }
        return new ActorDto(actor.getId(), actor.getName());
    }
}