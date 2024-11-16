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

	// Create Product
	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody ProductCreationRequest data) {
		return ResponseEntity.ok(productService.createProduct(data));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateProduct(@RequestPart("files") List<MultipartFile> files, @PathVariable Long id,
			@RequestPart("productData") String productData) {

		ObjectMapper objectMapper = new ObjectMapper();
		ProductUpdateRequest productRequest;

		try {
			productRequest = objectMapper.readValue(productData, ProductUpdateRequest.class);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

		return ResponseEntity.ok(productService.updateProduct(id, productRequest, files));
	}

	@GetMapping
	ApiResponse<Page<ProductResponse>> getProducts(@RequestParam Map<String, String> params,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {

		Page<ProductResponse> productsPage = productService.getProducts(params);

		return ApiResponse.<Page<ProductResponse>>builder().result(productsPage).build();
	}
	

	@DeleteMapping("/{productId}")
	ApiResponse<String> delete(@PathVariable Long productId) {
		return ApiResponse.<String>builder().result(productService.delete(productId)).build();
	}

	

}