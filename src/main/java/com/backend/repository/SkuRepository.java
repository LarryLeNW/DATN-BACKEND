package com.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.entity.Attribute;
import com.backend.entity.AttributeOption;
import com.backend.entity.Sku;

import jakarta.transaction.Transactional;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
	 List<Sku> findByProductId(Long productId);
}
