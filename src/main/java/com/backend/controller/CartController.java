package com.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.cart.CartCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.entity.Brand;
import com.backend.entity.Cart;
import com.backend.service.CartService;
import com.backend.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
	ApiResponse<Cart> create(@RequestBody CartCreationRequest request) {
		return ApiResponse.<Cart>builder().result(cartService.create(request)).build();
	}
	
	
}
