package com.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.ApiResponse;
import com.backend.dto.request.UserCreationRequest;
import com.backend.dto.request.UserUpdateRequest;
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
    ApiResponse<List<Product>> getProducts() {
        return ApiResponse.<List<Product>>builder()
                .result(productService.getProducts())
                .build();
    }

}
