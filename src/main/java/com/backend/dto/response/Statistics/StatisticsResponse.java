package com.backend.dto.response.Statistics;

import com.backend.dto.response.review.ReviewResponse;

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
public class StatisticsResponse {
	String month;
	int ordersCount;
	int customers;
	double revenue;
}
