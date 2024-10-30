package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.dto.request.comment.CommentCreationRequest;
import com.backend.dto.response.comment.CommentResponse;
import com.backend.entity.Blog;
import com.backend.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	Comment toComment(CommentCreationRequest request);
	
	@Mapping(source = "user.id", target = "user_id")
	@Mapping(source = "blog.blogId", target = "blog_id")
	CommentResponse toCommentResponse(Comment comment);
}
