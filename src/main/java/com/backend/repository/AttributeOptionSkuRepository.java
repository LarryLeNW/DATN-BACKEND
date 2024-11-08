package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.entity.Attribute;
import com.backend.entity.AttributeOption;
import com.backend.entity.AttributeOptionSku;
import com.backend.entity.AttributeOptionSkuKey;
import com.backend.entity.Category;

import jakarta.transaction.Transactional;
import java.util.List;
import com.backend.entity.Sku;


@Repository
public interface AttributeOptionSkuRepository extends JpaRepository<AttributeOptionSku, AttributeOptionSkuKey> {
	  Optional<AttributeOptionSku> findBySkuAndAttributeOption(Sku sku, AttributeOption attributeOption);
}
