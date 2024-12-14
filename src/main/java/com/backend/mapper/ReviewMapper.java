package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.backend.dto.request.review.ReviewCreationRequest;
import com.backend.dto.response.review.ReviewResponse;
import com.backend.entity.Review;
import com.backend.entity.User;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

	Review toReview(ReviewCreationRequest request);

	@Mapping(source = "reviewBy.avatar", target = "postBy.avatar")
	@Mapping(source = "reviewBy.email", target = "postBy.email  ")
	@Mapping(source = "reviewBy.id", target = "postBy.id")
    @Mapping(source = "reviewBy.id", target = "review_by_id") 
    @Mapping(source = "product.id", target = "product_id")
    ReviewResponse toReviewResponse(Review review);
	

}
	