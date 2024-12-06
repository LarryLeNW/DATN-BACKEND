package com.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.fqa.QuestionCreation;
import com.backend.dto.request.fqa.QuestionReplyCreation;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.question.QuestionReplyResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.entity.QuestionReply;
import com.backend.service.QuestionReplyService;
import com.backend.service.QuestionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/question-reply")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionReplyController {
	QuestionReplyService questionReplyService;

	@PostMapping
	ApiResponse<QuestionReplyResponse> create(@RequestBody QuestionReplyCreation request) {
		return ApiResponse.<QuestionReplyResponse>builder().result(questionReplyService.create(request)).build();
	}

	@GetMapping
	ApiResponse<PagedResponse<QuestionReply>> getAll(Map<String, String> params) {
		return ApiResponse.<PagedResponse<QuestionReply>>builder().result(questionReplyService.getAll(params)).build();
	}


}
