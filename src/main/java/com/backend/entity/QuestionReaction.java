package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.QuestionStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "QuestionReactions")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionReaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	@JoinColumn(name = "question_id")
	@JsonIgnore
	Question question;

	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;

	@Column(name = "reaction_type")
	String reactionType;

	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

}
