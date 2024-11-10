package com.backend.dto.request.blog;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.backend.entity.CategoryBlog;
import com.backend.entity.User;


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
public class BlogCreationRequest {

	@NotNull
	String title;

	@NotNull
	String content;

	int image;

	
	String userId;

	int categoryBlogId;
	
	LocalDateTime createdAt;
}
