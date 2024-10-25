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
import com.backend.dto.request.reply.ReplyCreationRequest;
import com.backend.dto.request.reply.ReplyUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.comment.CommentResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.reply.ReplyResponse;
import com.backend.entity.Comment;
import com.backend.entity.Reply;
import com.backend.service.CommentService;
import com.backend.service.ReplyService;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("api/replys")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReplyController {

	
	ReplyService replyService;
	

	@PostMapping
	ApiResponse<ReplyResponse> createReply (ReplyCreationRequest request){
		return ApiResponse.<ReplyResponse>builder().result(replyService.createReply(request)).build();
	}
	
	@PutMapping("/{replyId}")
	ApiResponse<ReplyResponse> updateComment(ReplyUpdateRequest request,int replyId){
		return ApiResponse.<ReplyResponse>builder().result(replyService.updateReply(request, replyId)).build();				
	}
	@DeleteMapping("/{replyId}")
	ApiResponse<String> deleteComment (int replyId){
		return ApiResponse.<String>builder().result("delete success comment ").build();
	}
	
	@GetMapping
	ApiResponse<PagedResponse<Reply>> getReplys(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {

		PagedResponse<Reply> pagedResponse = replyService.getReplys(page, limit, sort, search);

		return ApiResponse.<PagedResponse<Reply>>builder().result(pagedResponse).build();
	}

}
