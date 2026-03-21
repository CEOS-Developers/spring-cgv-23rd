package com.ceos23.spring_cgv_23rd.Actor.Repository;

import com.ceos23.spring_cgv_23rd.Actor.Domain.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorInterface extends JpaRepository<Actor, Long> {
}
