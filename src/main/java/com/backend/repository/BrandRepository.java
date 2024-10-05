package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
	boolean existsByName(String name);

}
