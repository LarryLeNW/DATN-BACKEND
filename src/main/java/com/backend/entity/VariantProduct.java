package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "variant_products")
public class VariantProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	@Column(name = "name", columnDefinition = "NVARCHAR(255)")
	String name;

	@Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
	String description;

	@Column(name = "price")
	double price;

	@Column(name = "stock")
	int stock;

	@Column(name = "thumbnail_url")
	String thumbnail_url;

	@ManyToOne
	Product product;

	@OneToMany( cascade = CascadeType.ALL)
	List<AttributeProduct> attributes;
	
	@CreationTimestamp
	LocalDateTime createdAt;

	@UpdateTimestamp
	LocalDateTime updatedAt;

}
