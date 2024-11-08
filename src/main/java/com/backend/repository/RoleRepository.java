package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

	Role findByName(String name);

}
