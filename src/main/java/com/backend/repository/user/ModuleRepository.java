package com.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Role;

@Repository
public interface ModuleRepository extends JpaRepository<com.backend.entity.Module, Long> {
	
}
