package com.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody ProductCreationRequest data) {
		return ResponseEntity.ok(productService.createProduct(data));
	}

	@PutMapping("/{productId}")
	public ResponseEntity<?> updateProduct(@PathVariable Long productId,@RequestBody ProductUpdateRequest data) {
		return ResponseEntity.ok(productService.updateProduct(productId, data));
	}

	@GetMapping("/{productId}")
	ApiResponse<ProductResponse> getProducts(@PathVariable Long productId) {
		return ApiResponse.<ProductResponse>builder().result(productService.getProductById(productId)).build();
	}
	
	@GetMapping
	ApiResponse<PagedResponse<ProductResponse>> getOneById(@RequestParam Map<String, String> params,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		
		return ApiResponse.<PagedResponse<ProductResponse>>builder().result(productService.getProducts(params)).build();
	}
	
	
	
	@DeleteMapping("/{productId}")
	ApiResponse<String> delete(@PathVariable Long productId) {
		return ApiResponse.<String>builder().result(productService.delete(productId)).build();
	}

	

}