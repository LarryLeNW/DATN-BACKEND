package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.dto.request.fqa.QuestionCreation;
import com.backend.dto.response.question.QuestionReactionResponse;
import com.backend.dto.response.question.QuestionReplyResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.entity.Question;
import com.backend.entity.QuestionReaction;
import com.backend.entity.QuestionReply;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
	Question toQuestion(QuestionCreation request);

	@Mapping(source = "user.username", target = "postBy.username")
	@Mapping(source = "user.avatar", target = "postBy.avatar")
	QuestionResponse toQuestionResponse(Question question);

	@Mapping(source = "user.username", target = "postBy.username")
    @Mapping(source = "user.avatar", target = "postBy.avatar")
    QuestionReplyResponse toQuestionReplyResponse(QuestionReply questionReply);

    @Mapping(source = "user.username", target = "postBy.username")
    @Mapping(source = "user.avatar", target = "postBy.avatar")
    QuestionReactionResponse toQuestionReactionResponse(QuestionReaction questionReaction);

    List<QuestionReplyResponse> toQuestionReplyResponses(List<QuestionReply> replies);
    List<QuestionReactionResponse> toQuestionReactionResponses(List<QuestionReaction> reactions);

}
