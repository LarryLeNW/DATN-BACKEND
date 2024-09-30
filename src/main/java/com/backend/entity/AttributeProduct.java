package com.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.controller.PermissionController;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "attribute_product")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class AttributeProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	@Column(name = "name", columnDefinition = "NVARCHAR(255)")
	String name;

	public AttributeProduct(String name, String value, Product product) {
	    if (product != null && variant_product == null) {
	        this.name = name;
	        this.value = value;
	        this.product = product;
	    } else {
	        throw new IllegalArgumentException("Product cannot be set when VariantProduct is also provided");
	    }
	}

	public AttributeProduct(String name, String value, VariantProduct variant_product) {
	    if (variant_product != null && product == null) {
	        this.name = name;
	        this.value = value;
	        this.variant_product = variant_product;
	    } else {
	        throw new IllegalArgumentException("VariantProduct cannot be set when Product is also provided");
	    }
	}

	@Column(name = "value", columnDefinition = "NVARCHAR(255)")
	String value;

    @JsonIgnore
	@ManyToOne(optional = true)
	Product product ; 

    @JsonIgnore
	@ManyToOne(optional = true)
	VariantProduct variant_product; 
	
	@CreationTimestamp
	LocalDateTime createdAt;

	@UpdateTimestamp
	LocalDateTime updatedAt;
}
