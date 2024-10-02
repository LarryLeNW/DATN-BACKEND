package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.VariantProduct;

@Repository
public interface VariantProductRepository extends JpaRepository<VariantProduct, String>{

}
