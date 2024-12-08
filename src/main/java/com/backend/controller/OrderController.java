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
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {

	OrderService orderService;

	@Autowired
	ObjectMapper objectMapper;

	@GetMapping
	ApiResponse<PagedResponse<OrderResponse>> getAlls(
			@RequestParam Map<String, String> params) {

		PagedResponse<OrderResponse> pagedResponse = orderService.getOrders(params);

		return ApiResponse.<PagedResponse<OrderResponse>>builder().result(pagedResponse).build();
	}

	@PostMapping
	ApiResponse<String> createOrder(@RequestBody OrderCreationRequest requestData , HttpServletRequest request) throws ClientProtocolException, IOException {
		return ApiResponse.<String>builder().result(orderService.createOrder(requestData, request)).build();
	}

	@PutMapping("/{orderId}")
	ApiResponse<OrderResponse> updateOrder(@RequestBody OrderUpdateRequest request, @PathVariable Integer orderId) {
		return ApiResponse.<OrderResponse>builder().result(orderService.updateOrder(orderId, request)).build();
	}

	@DeleteMapping("/{orderId}")
	public ApiResponse<String> deleteOrder(@PathVariable Integer orderId) {
		orderService.deleteOrder(orderId);
		return ApiResponse.<String>builder().result("delete success order with id of: " + orderId).build();
	}

	@GetMapping("/{orderId}")
	ApiResponse<OrderResponse> getOrderById(@PathVariable Integer orderId) {
		return ApiResponse.<OrderResponse>builder().result(orderService.getOrderById(orderId)).build();
	}
	
	@GetMapping("/code/{codeId}")
	ApiResponse<OrderResponse> getOrderByCode(@PathVariable String codeId) {
		return ApiResponse.<OrderResponse>builder().result(orderService.getOrderByCode(codeId)).build();
	}

	@GetMapping("/getAllStatusOrder")
	public ResponseEntity<List<OrderStatusType>> getAllOrderStatuses() {
		List<OrderStatusType> orderStatuses = Arrays.asList(OrderStatusType.values());
		return ResponseEntity.ok(orderStatuses);
	}
	
	
	


}
