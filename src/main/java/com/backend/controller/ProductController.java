package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.Product;
import com.backend.service.ProductService;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping
	public ResponseEntity<Page<ProductResponse>> getProducts(@RequestParam Map<String, String> params,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {

		Page<ProductResponse> productsPage = productService.getProducts(params);
		return ResponseEntity.ok(productsPage);
	}

	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody ProductCreationRequest product) {
		return ResponseEntity.ok(productService.createProduct(product));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") Long productId,
			@RequestBody ProductCreationRequest request) {
		ProductResponse updatedProduct = productService.updateProduct(productId, request);
		return ResponseEntity.ok(updatedProduct);
	}

}

