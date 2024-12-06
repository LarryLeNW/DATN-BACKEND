package com.backend.dto.response.question;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.entity.QuestionReaction;
import com.backend.entity.QuestionReply;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionReplyResponse {

	Long id;

	PostBy postBy;

	String replyText;

	String images;

	LocalDateTime createdAt;

	LocalDateTime updatedAt;

	List<QuestionReplyResponse> childReplies;

}
