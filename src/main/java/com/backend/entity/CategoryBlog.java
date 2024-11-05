package com.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "categoryBlog")
@Data
public class CategoryBlog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int categoryBlogId;
	
	@NotNull
	@Column(name = "name" , columnDefinition = "NVARCHAR(MAX)")
	private String name;
	
	@Column(name = "description" ,columnDefinition = "NVARCHAR(MAX)")
	String description;
	
	@CreationTimestamp	
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	LocalDateTime updatedAt;
	
	
}
