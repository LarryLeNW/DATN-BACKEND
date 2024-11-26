package com.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.cart.CartCreationRequest;
import com.backend.dto.request.cart.CartUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Brand;
import com.backend.entity.Cart;
import com.backend.service.CartService;
import com.backend.service.UserService;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {
	
	@Autowired
	CartService cartService; 
	
	@PostMapping
	ApiResponse<PagedResponse<CartDetailResponse>> create(@RequestBody CartCreationRequest request) {
		return ApiResponse.<PagedResponse<CartDetailResponse>>builder().result(cartService.create(request)).build();
	}

	@PutMapping
	ApiResponse<PagedResponse<CartDetailResponse>> create(@RequestBody CartUpdateRequest request) {
		return ApiResponse.<PagedResponse<CartDetailResponse>>builder().result(cartService.update(request)).build();
	}
	
	@GetMapping
	ApiResponse<PagedResponse<CartDetailResponse>> getAlls(
			@RequestParam Map<String, String> params,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {

		PagedResponse<CartDetailResponse> pagedResponse = (PagedResponse<CartDetailResponse>) cartService.getAll(params);

		return ApiResponse.<PagedResponse<CartDetailResponse>>builder().result(pagedResponse).build();
	}
	
	@DeleteMapping("/{cartId}")
	ApiResponse<PagedResponse<CartDetailResponse>> delete(@PathVariable Long cartId) {
		return ApiResponse.<PagedResponse<CartDetailResponse>>builder().result(cartService.delete(cartId)).build();
	}

		
	
}
