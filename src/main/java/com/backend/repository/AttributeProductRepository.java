package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.entity.Attribute;

import jakarta.transaction.Transactional;
import java.util.List;


@Repository
public interface AttributeProductRepository extends JpaRepository<Attribute, Long> {
	Optional<Attribute> findByName(String name);
}
