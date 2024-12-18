package com.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.blog.comment.CommentCreationRequest;
import com.backend.dto.request.blog.comment.CommentUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.blog.comment.CommentResponse;
import com.backend.dto.response.blog.reply.ReplyResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Category;
import com.backend.entity.Comment;
import com.backend.mapper.CommentMapper;
import com.backend.service.CategoryService;
import com.backend.service.CommentService;
import com.backend.service.ReplyService;
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
	ReplyService replyService;

    private final CommentMapper commentMapper;  // Inject the CommentMapper

	@PostMapping
	ApiResponse<CommentResponse> createComment(@RequestBody CommentCreationRequest request) {
		return ApiResponse.<CommentResponse>builder().result(commentService.createComment(request)).build();
	}

	@PutMapping("/{commentId}")
	ApiResponse<CommentResponse> updateComment(@RequestBody CommentUpdateRequest request,
			@PathVariable Integer commentId) {
		return ApiResponse.<CommentResponse>builder().result(commentService.updateComment(request, commentId)).build();
	}

	@DeleteMapping("/{commentId}")
	ApiResponse<String> deleteComment(@PathVariable Integer commentId) {
		commentService.deleteComment(commentId);
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
	
	@GetMapping("/blog/{blogId}")
	public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Integer blogId) {
	    List<Comment> comments = commentService.getCommentsByBlogId(blogId);

	    // Map comments to responses and add nested replies for each reply
	    List<CommentResponse> commentResponses = comments.stream()
	        .map(comment -> {
	            CommentResponse commentResponse = commentMapper.toCommentResponse(comment);
	            List<ReplyResponse> replies = replyService.getRepliesByCommentId(comment.getCommentId());

	            // Set the replies (including nested ones)
	            commentResponse.setReplyResponse(replies);
	            return commentResponse;
	        })
	        .collect(Collectors.toList());

	    return ResponseEntity.ok(commentResponses);
	}

}
