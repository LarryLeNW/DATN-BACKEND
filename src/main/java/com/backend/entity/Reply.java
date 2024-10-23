package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "replys")
@Data
public class Reply {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int replyId;

	private String content;

	@ManyToOne
	@JoinColumn(name = "commentId")
	@JsonIgnore
	private Comment parentComment;

	@ManyToOne
	@JoinColumn(name = "userId")
	@JsonIgnore
	private User user;

	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@CreationTimestamp
	@Column(name = "update_at")
	LocalDateTime updateAt;

}
