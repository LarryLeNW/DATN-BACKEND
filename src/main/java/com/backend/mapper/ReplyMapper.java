package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.dto.request.reply.ReplyCreationRequest;
import com.backend.dto.response.reply.ReplyResponse;
import com.backend.entity.Reply;

@Mapper(componentModel = "spring")
public interface ReplyMapper {

	Reply toReply(ReplyCreationRequest request);
	
	@Mapping(source = "comment.commentId", target = "comment_id")
	@Mapping(source = "user.id", target = "user_id")
	ReplyResponse toReplyResponse(Reply reply);
}
