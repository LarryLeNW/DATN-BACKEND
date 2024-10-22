	package com.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "blogs")
@Data
public class Blog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int blogId;

	@NotNull
	@Column(name = "title")
	private String title;

	@NotNull
	@Column(name = "content")
	private String content;

	@Column(name = "images",  columnDefinition = "NVARCHAR(MAX)")
	private String images;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	@JsonIgnore
	private User user;

	@ManyToOne
	@JoinColumn(name = "categoryBlogId", nullable = false)
	@JsonIgnore
	private CategoryBlog categoryBlog;

	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

}
