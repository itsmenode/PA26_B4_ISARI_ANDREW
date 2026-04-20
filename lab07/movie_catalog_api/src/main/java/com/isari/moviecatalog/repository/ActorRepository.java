package com.isari.moviecatalog.repository;

import com.isari.moviecatalog.domain.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Integer> {

    Optional<Actor> findByName(String name);
}
