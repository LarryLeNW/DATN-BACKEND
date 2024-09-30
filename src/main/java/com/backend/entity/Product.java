package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	@Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
	String description;

	@Column(name = "price")
	double price;

	@Column(name = "stock")
	int stock;

	@Column(name = "thumbnail_url")
	String thumbnail_url;
	
	@ManyToOne
	@JsonBackReference
	Category category;

	@ManyToOne
	@JsonBackReference
	Brand brand;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference 
	List<AttributeProduct> attributes;

	@ManyToOne(optional = true)
	@JsonBackReference 
	Product product;

	@ManyToOne(optional = true)
	@JsonManagedReference 
	VariantProduct variant_product;


	@CreationTimestamp
	LocalDateTime createdAt;

	@UpdateTimestamp
	LocalDateTime updatedAt;

}
