package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;


import org.mapstruct.Mapping;

import com.backend.dto.request.blog.comment.CommentCreationRequest;
import com.backend.dto.response.blog.comment.CommentResponse;
import com.backend.dto.response.blog.reply.ReplyResponse;
import com.backend.entity.Blog;
import com.backend.entity.Comment;
import com.backend.entity.Reply;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	Comment toComment(CommentCreationRequest request);
	
	@Mapping(source = "user.id", target = "user_id")
	@Mapping(source = "blog.blogId", target = "blog_id")
	@Mapping(source = "user.username", target = "userName")
	@Mapping(source = "user.avatar",target = "avatar")
    @Mapping(target = "replyResponse", source = "replies")
	CommentResponse toCommentResponse(Comment comment);
	
	List<ReplyResponse> toReplyResponse(List<Reply> replies);
}
