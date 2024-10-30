package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {

	Optional<Team> findById(Long id);

}
