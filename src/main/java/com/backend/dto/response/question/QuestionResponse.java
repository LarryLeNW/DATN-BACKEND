package com.backend.dto.response.question;

import java.time.LocalDateTime;
import java.util.List;

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
public class QuestionResponse {

	Long id ;
	
	String questionText;

	String images;

	PostBy postBy;

	LocalDateTime updatedAt;
	
	LocalDateTime createdAt;
	
	Boolean isAccepted;
	
	List<QuestionReplyResponse> replies;
	
	List<QuestionReactionResponse> reactions;
}
