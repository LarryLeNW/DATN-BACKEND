package com.backend.mapper;

import org.mapstruct.Mapper;


import com.backend.dto.request.categoryBlog.CategoryBlogCreationRequest;
import com.backend.entity.CategoryBlog;

@Mapper(componentModel = "spring")
public interface CategoryBlogMapper {

	CategoryBlog toCategoryBlog(CategoryBlogCreationRequest request);
	
}
