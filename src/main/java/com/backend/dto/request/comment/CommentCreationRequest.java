package com.backend.dto.request.comment;

import java.time.LocalDateTime;

import com.backend.dto.request.categoryBlog.CategoryBlogCreationRequest;

import jakarta.validation.constraints.NotNull;
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
public class CommentCreationRequest {

	@NotNull
	String content;
	
	int blogId;
	
	String userId;
	
	LocalDateTime createdAt;
	LocalDateTime updateAt;
}