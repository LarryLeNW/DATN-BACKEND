package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.backend.dto.request.fqa.QuestionCreation;
import com.backend.dto.response.question.QuestionReactionResponse;
import com.backend.dto.response.question.QuestionReplyResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.entity.Question;
import com.backend.entity.QuestionReaction;
import com.backend.entity.QuestionReply;
import com.backend.entity.User;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
	Question toQuestion(QuestionCreation request);

	@Mapping(source = "user", target = "postBy.username", qualifiedByName = "getUsernameOrEmail")
	@Mapping(source = "user.avatar", target = "postBy.avatar")
	QuestionResponse toQuestionResponse(Question question);

	@Mapping(source = "user", target = "postBy.username", qualifiedByName = "getUsernameOrEmail")
	@Mapping(source = "user.avatar", target = "postBy.avatar")
	@Mapping(source = "user.id", target = "postBy.id")
	QuestionReplyResponse toQuestionReplyResponse(QuestionReply questionReply);

	@Mapping(source = "user", target = "postBy.username", qualifiedByName = "getUsernameOrEmail")
	@Mapping(source = "user.avatar", target = "postBy.avatar")
	@Mapping(source = "user.id", target = "postBy.id")
	QuestionReactionResponse toQuestionReactionResponse(QuestionReaction questionReaction);

	List<QuestionReplyResponse> toQuestionReplyResponses(List<QuestionReply> replies);

	List<QuestionReactionResponse> toQuestionReactionResponses(List<QuestionReaction> reactions);

	@Named("getUsernameOrEmail")
	default String getUsernameOrEmail(User user) {
		if (user.getUsername() != null && !user.getUsername().isEmpty()) {
			return user.getUsername();
		}
		return user.getEmail().split("@")[0];
	}
}
