package com.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

	@CreationTimestamp	
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	LocalDateTime updatedAt;
	
	@Override
	public String toString() {
	    return "Product{id=" + id + ", name='" + name + "'}"; 
	}
	
	
	
	
}
