package com.backend.dto.request.blog.reply;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) 
public class ReplyCreationRequest {

	@NotNull
	String content;
	
	int commentId;
	
	String userId;
	
	int parentReplyId;
	

}
