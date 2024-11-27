package com.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.blog.reply.ReplyCreationRequest;
import com.backend.dto.request.blog.reply.ReplyUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.blog.reply.ReplyResponse;
import com.backend.dto.response.common.PagedResponse;
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
	ApiResponse<ReplyResponse> createReply(@RequestBody ReplyCreationRequest request) {
		return ApiResponse.<ReplyResponse>builder().result(replyService.createReply(request)).build();
	}

	@PutMapping("/{replyId}")
	ApiResponse<ReplyResponse> updateComment(@RequestBody ReplyUpdateRequest request, @PathVariable Integer replyId) {
		return ApiResponse.<ReplyResponse>builder().result(replyService.updateReply(request, replyId)).build();
	}

	@DeleteMapping("/{replyId}")
	ApiResponse<String> deleteComment(@PathVariable Integer replyId) {
		replyService.deleteReply(replyId);
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

	@GetMapping("/comment/{commentId}")
	ApiResponse<List<ReplyResponse>> getRepliesByComment(@PathVariable Integer commentId) {
	    return ApiResponse.<List<ReplyResponse>>builder()
	                      .result(replyService.getRepliesByCommentId(commentId))
	                      .build();
	}


}
