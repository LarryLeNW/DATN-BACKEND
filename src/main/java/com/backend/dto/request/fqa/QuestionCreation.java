package com.backend.dto.request.fqa;

import com.backend.constant.Type.QuestionStatusType;
import com.backend.dto.request.categoryBlog.CategoryBlogUpdateRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionCreation {
	String questionText;
	String images;
	QuestionStatusType status = QuestionStatusType.ACTIVE;
	
	
}
