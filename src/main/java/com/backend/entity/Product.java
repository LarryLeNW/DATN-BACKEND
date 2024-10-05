package com.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "products")
@Data
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", columnDefinition = "NVARCHAR(MAX)")
	private String name;

	private String slug;

	@ManyToOne
	@JoinColumn(name = "categoryId", nullable = false)
	@JsonIgnore
	private Category category;
	
	@ManyToOne
	@JoinColumn(name = "brandId", nullable = false)
	@JsonIgnore
	private Brand brand;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Sku> skus;

}
