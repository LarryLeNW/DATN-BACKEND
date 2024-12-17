package com.backend.dto.response.review;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {

	PostBy postBy;
	
	String id;
	
	int rating;
	
	String review_text;

	String review_by_id;
	
	String product_id;

	String images;
	
	LocalDateTime createdAt;
	
}
