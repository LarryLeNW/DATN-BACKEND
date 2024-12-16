package com.backend.controller;

import java.io.IOException;
import java.util.Arrays;

import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.backend.constant.Type.OrderStatusType;
import com.backend.constant.Type.RentalStatus;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.request.rental.RentalCreation;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.rental.RentalResponse;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.service.OrderService;
import com.backend.service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/rental")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RentalController {

	RentalService rentalService;

	@PostMapping
	ApiResponse<String> createOrder(@RequestBody RentalCreation requestData, HttpServletRequest request)
			throws ClientProtocolException, IOException {
		return ApiResponse.<String>builder().result(rentalService.create(requestData, request)).build();
	}

	@PutMapping("/{id}/{status}")
	ApiResponse<String> updateStatus(@PathVariable Long id, @PathVariable RentalStatus status) {
		return ApiResponse.<String>builder().result(rentalService.updateStatus(id, status)).build();
	}

	@GetMapping
	ApiResponse<PagedResponse<RentalResponse>> getAlls(@RequestParam Map<String, String> params) {
		PagedResponse<RentalResponse> pagedResponse = rentalService.getAll(params);
		return ApiResponse.<PagedResponse<RentalResponse>>builder().result(pagedResponse).build();
	}

	@GetMapping("/{rentalId}")
	ApiResponse<RentalResponse> getOrderById(@PathVariable Long rentalId) {
		return ApiResponse.<RentalResponse>builder().result(rentalService.getRentalById(rentalId)).build();
	}

	@GetMapping("/statistics/status")
	public ResponseEntity<Map<RentalStatus, Long>> getOrderStatistics(   @RequestParam Map<String, String> params) {
	    Map<RentalStatus, Long> statistics = rentalService.getRentalStatistics(params);
	    return ResponseEntity.ok(statistics);
	}
	
	@GetMapping("/statistics/totals")
	public ResponseEntity<Map<String, Long>> getOrderTotals() {
	    Map<String, Long> totals = rentalService.getOrderTotals();
	    return ResponseEntity.ok(totals);
	}
	
	@GetMapping("/statistics/daily")
	public ResponseEntity<Map<Integer, Long>> getDailyOrderStatistics(
	        @RequestParam("month") int month,
	        @RequestParam("year") int year) {
	    Map<Integer, Long> dailyStatistics = rentalService.getOrdersByDayInMonth(month, year);
	    return ResponseEntity.ok(dailyStatistics);
	}

	
}
