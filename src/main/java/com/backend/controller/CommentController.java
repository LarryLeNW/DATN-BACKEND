package com.backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.comment.CommentCreationRequest;
import com.backend.dto.request.comment.CommentUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.comment.CommentResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Category;
import com.backend.entity.Comment;
import com.backend.service.CategoryService;
import com.backend.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentController {

	CommentService commentService;
	
	@PostMapping
	ApiResponse<CommentResponse> createComment (CommentCreationRequest request){
		return ApiResponse.<CommentResponse>builder().result(commentService.createComment(request)).build();
	}
	
	@PutMapping("/{commentId}")
	ApiResponse<CommentResponse> updateComment(CommentUpdateRequest request,Integer commentId){
		return ApiResponse.<CommentResponse>builder().result(commentService.updateComment(request, commentId)).build();				
	}
	@DeleteMapping("/{commentId}")
	ApiResponse<String> deleteComment (Integer commentId){
		return ApiResponse.<String>builder().result("delete success comment ").build();
	}
	
	@GetMapping
	ApiResponse<PagedResponse<Comment>> getComments(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {

		PagedResponse<Comment> pagedResponse = commentService.getComment(page, limit, sort, search);

		return ApiResponse.<PagedResponse<Comment>>builder().result(pagedResponse).build();
	}

}
