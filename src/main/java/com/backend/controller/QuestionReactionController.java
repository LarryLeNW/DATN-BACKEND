package com.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.fqa.QuestionCreation;
import com.backend.dto.request.fqa.QuestionReactionCreation;
import com.backend.dto.request.fqa.QuestionReplyCreation;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.question.QuestionReactionResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.entity.QuestionReaction;
import com.backend.entity.QuestionReply;
import com.backend.service.QuestionReactionService;
import com.backend.service.QuestionReplyService;
import com.backend.service.QuestionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/question-reaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionReactionController {
	QuestionReactionService questionReactionService;

	@PostMapping
	ApiResponse<QuestionReactionResponse> create(@RequestBody QuestionReactionCreation request) {
		return ApiResponse.<QuestionReactionResponse>builder().result(questionReactionService.create(request)).build();
	}

	@PutMapping("/{id}")
	ApiResponse<QuestionReactionResponse> update(@PathVariable Long id, @RequestBody QuestionReactionCreation request) {
		return ApiResponse.<QuestionReactionResponse>builder().result(questionReactionService.update(id, request))
				.build();
	}

}
