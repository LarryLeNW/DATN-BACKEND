package com.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.dto.request.review.ReviewCreationRequest;
import com.backend.dto.request.review.ReviewUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.review.ReviewResponse;
import com.backend.entity.Product;
import com.backend.entity.Review;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ReviewMapper;
import com.backend.repository.ProductRepository;
import com.backend.repository.ReviewRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.utils.Helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewService {
	ReviewRepository reviewRepository;
	ReviewMapper reviewMapper;
	UserRepository userRepository;
	ProductRepository productRepository;
	EntityManager entityManager;
	
	public PagedResponse<Review> getReview(int page, int limit, String sort, String... search) {

		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Review> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Review> query = customSearchService.buildSearchQuery(Review.class, search, sort);

		List<Review> reviews = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
				.setMaxResults(limit).getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Review.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(reviews, page, totalPages, totalElements, limit);
	}

	public ReviewResponse createReview(ReviewCreationRequest request) {
		Review review = reviewMapper.toReview(request);

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

		review.setReviewBy(user);
		review.setProduct(product);

			review = reviewRepository.save(review);
		return reviewMapper.toReviewResponse(review);

	}

	public ReviewResponse updateReview(ReviewUpdateRequest request, String reviewId) {

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
		
		Helpers.updateEntityFields(request, review); 
		
		try {
			review = reviewRepository.save(review);
		} catch (Exception e) {
			// TODO: handle exception
			throw new AppException(ErrorCode.REVIEW_NOT_FOUND);
		}
		
		return reviewMapper.toReviewResponse(review);
	}

	public void deleteReview(String reviewId) {
		reviewRepository.deleteById(reviewId);
	}

}
