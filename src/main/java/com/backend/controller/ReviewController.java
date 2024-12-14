package com.backend.controller;

import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.request.review.ReviewCreationRequest;
import com.backend.dto.request.review.ReviewUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.review.ReviewResponse;
import com.backend.entity.Product;
import com.backend.entity.Review;
import com.backend.service.ProductService;
import com.backend.service.ReviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Validated
public class ReviewController {
	ReviewService reviewService;

	@GetMapping
	ApiResponse<PagedResponse<ReviewResponse>> getAlls(@RequestParam Map<String, String> params) {

		PagedResponse<ReviewResponse> pagedResponse = (reviewService.getAll(params));

		return ApiResponse.<PagedResponse<ReviewResponse>>builder().result(pagedResponse).build();
	}

	@PostMapping
	ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewCreationRequest request) {
		return ApiResponse.<ReviewResponse>builder().result(reviewService.createReview(request)).build();
	}

//	@DeleteMapping("/{reviewId}")
//	ApiResponse<String> deleteReview(@PathVariable String reviewId) {
//		reviewService.deleteReview(reviewId);
//		return ApiResponse.<String>builder().result("Review has been deleted").build();
//	}
//
	@GetMapping("/{reviewId}")
	ApiResponse<ReviewResponse> getOrderById(@PathVariable String reviewId) {
		return ApiResponse.<ReviewResponse>builder().result(reviewService.getReviewById(reviewId)).build();
	}

}
