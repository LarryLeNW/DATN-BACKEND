package com.backend.dto.request.fqa;

import com.backend.constant.Type.QuestionStatusType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionReplyCreation {
	
	String images;
	
	String replyText;
	
	Long questionId; 
	
	Long parentId;
	
	Boolean isAccepted = true;
}
