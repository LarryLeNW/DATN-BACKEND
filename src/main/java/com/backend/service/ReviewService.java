package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.dto.request.review.ReviewCreationRequest;
import com.backend.dto.request.review.ReviewUpdateRequest;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.review.ReviewResponse;
import com.backend.entity.Cart;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Product;
import com.backend.entity.Review;
import com.backend.entity.User;
import com.backend.entity.rental.RentalDetail;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ReviewMapper;
import com.backend.repository.ReviewRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.order.OrderDetailRepository;
import com.backend.repository.product.ProductRepository;
import com.backend.repository.rental.RentalDetailRepository;
import com.backend.repository.user.UserRepository;
import com.backend.specification.CartSpecification;
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
	RentalDetailRepository rentalDetailRepository;
	OrderDetailRepository orderDetailRepository;
	EntityManager entityManager;

	public PagedResponse<ReviewResponse> getAll(Map<String, String> params) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "createdAt");
		String orderBy = params.getOrDefault("orderBy", "desc");

		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);

		Specification<Review> spec = Specification.where(null);

		if (params.containsKey("productId")) {
			Long productId = Long.parseLong(params.get("productId"));
			spec = spec.and(
					(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("product").get("id"), productId));
		}

		if (params.containsKey("stars")) {
			int stars = Integer.parseInt(params.get("stars"));
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("rating"), stars));
		}

		Page<Review> reviwePage = reviewRepository.findAll(spec, pageable);
		List<ReviewResponse> reviewResponses = reviwePage.getContent().stream().map(reviewMapper::toReviewResponse)
				.collect(Collectors.toList());

		return new PagedResponse<>(reviewResponses, page + 1, reviwePage.getTotalPages(), reviwePage.getTotalElements(),
				limit);
	}

	public ReviewResponse createReview(ReviewCreationRequest request) {
		Review review = reviewMapper.toReview(request);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

		review.setReviewBy(user);
		review.setProduct(product);

		Review reviewCreated = reviewRepository.save(review);

		if (request.getDetailRentalId() != null) {
			RentalDetail rentalDetailUpdated = rentalDetailRepository.findById(request.getDetailRentalId())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuê này nữa"));

			rentalDetailUpdated.setIsReview(true);
			rentalDetailRepository.save(rentalDetailUpdated);
		}

		if (request.getDetailOrderId() != null) {
			OrderDetail orderDetailUpdated = orderDetailRepository.findById(request.getDetailOrderId())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuê này nữa"));

			orderDetailUpdated.setIsReview(true);
			orderDetailRepository.save(orderDetailUpdated);
		}

		double averageStars = calculateAverageStars(product.getId());
		product.setStars(averageStars);
		productRepository.save(product);

		return reviewMapper.toReviewResponse(reviewCreated);

	}

	public double calculateAverageStars(Long productId) {
		List<Review> reviews = reviewRepository.findByProductId(productId);

		if (reviews.isEmpty()) {
			return 5;
		}

		double totalStars = reviews.stream().mapToInt(Review::getRating).sum();
	    return Math.round(totalStars / reviews.size() * 10.0) / 10.0;
	}

	public ReviewResponse getReviewById(String reviewId) {
		String idUser = SecurityContextHolder.getContext().getAuthentication().getName();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();

		Review reviewFound = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy review"));

		return reviewMapper.toReviewResponse(reviewFound);
	}

}
