package com.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.auth.Role.RoleCreationRequest;
import com.backend.dto.request.fqa.QuestionCreation;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.auth.RoleResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.entity.Question;
import com.backend.entity.Role;
import com.backend.service.QuestionService;
import com.backend.service.RoleService;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionController {
	QuestionService questionService;

	@PostMapping
	ApiResponse<QuestionResponse> create(@RequestBody QuestionCreation request) {
		return ApiResponse.<QuestionResponse>builder().result(questionService.create(request)).build();
	}

	@GetMapping
	ApiResponse<PagedResponse<QuestionResponse>> getAll(@RequestParam Map<String, String> params) {
		return ApiResponse.<PagedResponse<QuestionResponse>>builder().result(questionService.getAll(params)).build();
	}

}
