package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.entity.AttributeProduct;

@Repository
public interface AttributeProductRepository extends JpaRepository<AttributeProduct, String> {
	boolean existsByName(String name);

	boolean existsByNameAndProductId(String name, String id);
}
