package com.backend.dto.request.categoryBlog;

import com.backend.dto.request.category.CategoryCreationRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CategoryBlogCreationRequest {

	
	int categoryBlogId;
	
	@NotNull
	@Size(min = 3 , max = 255 , message = "@Param be greater 3 characters")
	String name;
	
	String description;
	
	

}
