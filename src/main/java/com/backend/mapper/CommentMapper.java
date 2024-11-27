package com.backend.mapper;

import org.mapstruct.Mapper;


import org.mapstruct.Mapping;

import com.backend.dto.request.blog.comment.CommentCreationRequest;
import com.backend.dto.response.blog.comment.CommentResponse;
import com.backend.entity.Blog;
import com.backend.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	Comment toComment(CommentCreationRequest request);
	
	@Mapping(source = "user.id", target = "user_id")
	@Mapping(source = "blog.blogId", target = "blog_id")
	@Mapping(source = "user.username", target = "userName")
	CommentResponse toCommentResponse(Comment comment);
}
