package com.backend.controller;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.service.CategoryService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryController {
	CategoryService categoryService;

	@GetMapping
	ApiResponse<List<Category>> getCategories() {
		return ApiResponse.<List<Category>>builder().result(categoryService.getCategories()).build();
	}

	@PostMapping
	ApiResponse<Category> createCategory(@Valid @RequestBody CategoryCreationRequest request) {
		return ApiResponse.<Category>builder().result(categoryService.createCategory(request)).build();
	}
}
