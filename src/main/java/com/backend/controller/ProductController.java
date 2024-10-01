package com.backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.Product;
import com.backend.service.ProductService;
import com.backend.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Validated
public class ProductController {
	ProductService productService;

	@GetMapping
	public ApiResponse<PagedResponse<Product>> getAllProducts(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {

		PagedResponse<Product> pagedResponse = productService.getProducts(page, limit, sort, search);

		return ApiResponse.<PagedResponse<Product>>builder().result(pagedResponse).build();
	}

	@PostMapping
	ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreationRequest request) {
		return ApiResponse.<ProductResponse>builder().result(productService.createProduct(request)).build();
	}

	@DeleteMapping("/{productId}")
	ApiResponse<String> deleteProduct(@PathVariable String productId) { 
		productService.deleteProduct(productId);
		return ApiResponse.<String>builder().result("Product has been deleted").build();
	}
	
	@PutMapping("/{productId}")
	ApiResponse<ProductResponse> updateProduct(@PathVariable String productId, @Valid @RequestBody ProductUpdateRequest request) {
		return ApiResponse.<ProductResponse>builder().result(productService.updateProduct(productId, request)).build();
	}

}
