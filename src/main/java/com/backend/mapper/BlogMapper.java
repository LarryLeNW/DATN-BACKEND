package com.backend.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.dto.request.blog.BlogCreationRequest;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.entity.Blog;

@Mapper(componentModel = "spring")
public interface BlogMapper {
	
	
	Blog toBlogRequest(BlogCreationRequest request);
	
	
	@Mapping(source = "user.id",target = "user_id")
	@Mapping(source = "categoryBlog.categoryBlogId",target = "categoryBlog_id")
	BlogResponse toBlogResponse(Blog blog);
	

}
