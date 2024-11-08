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
import lombok.Data;

@Entity
@Table(name = "comments")
@Data
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int commentId;

	@Column(name = "content")
	private String content;

	@ManyToOne
	@JoinColumn(name = "blogId", nullable = false)
	@JsonIgnore
	private Blog blog;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	@JsonIgnore
	private User user;

	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@CreationTimestamp
	@Column(name = "update_at")
	LocalDateTime updateAt;

}
