package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Order;
import com.backend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {

		PagedResponse<OrderResponse> pagedResponse = orderService.getOrders(page, limit, sort, search);

		return ApiResponse.<PagedResponse<OrderResponse>>builder().result(pagedResponse).build();
	}
	
	
	@PostMapping
	ApiResponse<OrderResponse> create(@RequestBody OrderCreationRequest request){
		return ApiResponse.<OrderResponse>builder().result(orderService.createOrder(request)).build();
	}
	
	@PutMapping("/{orderId}")
	ApiResponse<OrderResponse> update(@RequestBody OrderUpdateRequest request,@PathVariable Long orderId ){
		return ApiResponse.<OrderResponse>builder().result(orderService.updateOrder(orderId, request)).build();
	}
	
	@DeleteMapping("/{orderId}")
	public ApiResponse<String> delete(@PathVariable Long orderId) {
		orderService.deleteOrder(orderId);
		return ApiResponse.<String>builder().result("delete success order with id of: " + orderId).build();
	}
	
	
}
