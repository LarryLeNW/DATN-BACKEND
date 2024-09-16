package com.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.ApiResponse;
import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.UserResponse;
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
public class ProductController {
	ProductService productService;

    
    @GetMapping
    public ApiResponse<List<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String[] search) {

        return ApiResponse.<List<Product>>builder()
                .result(productService.getProducts(page, limit, sortBy, search))
                .build();
    }
    
    
    @PostMapping
    ApiResponse<Product> createUser(@Valid @RequestBody ProductCreationRequest request) {
    	        return ApiResponse.<Product>builder()
                .result(productService.createProduct(request))
                .build();
    }
    
}
