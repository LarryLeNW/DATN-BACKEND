package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.QuestionStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "Questions")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;

	@Column(name = "question_text", columnDefinition = "NVARCHAR(MAX)")
	String questionText;

	@Column(name = "images", columnDefinition = "NVARCHAR(MAX)")
	String images;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	List<QuestionReply> replies;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	List<QuestionReaction> reactions;

	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	QuestionStatusType status;
}
