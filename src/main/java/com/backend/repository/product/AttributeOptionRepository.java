package com.backend.repository.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.entity.Attribute;
import com.backend.entity.AttributeOption;
import com.backend.entity.Category;

import jakarta.transaction.Transactional;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long> {

	Optional<AttributeOption> findByValueAndAttribute(String attributeValue, Attribute attribute);
}
