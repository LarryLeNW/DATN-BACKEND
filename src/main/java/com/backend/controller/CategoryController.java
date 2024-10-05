package com.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;
import com.backend.dto.request.product.ProductCreationRequest;

import com.backend.dto.response.ApiResponse;
import com.backend.entity.Category;
import com.backend.service.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
//
//	@GetMapping
//	ApiResponse<PagedResponse<Category>> getCategories(
//			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
//			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
//			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {
//
//		PagedResponse<Category> pagedResponse = categoryService.getCategories(page, limit, sort, search);
//
//		return ApiResponse.<PagedResponse<Category>>builder().result(pagedResponse).build();
//	}

	@PostMapping
	ApiResponse<Category> createCategory(@Valid @RequestBody CategoryCreationRequest request) {
		return ApiResponse.<Category>builder().result(categoryService.createCategory(request)).build();
	}

//	@DeleteMapping("/{categoryId}")
//	ApiResponse<String> deleteCategory(@PathVariable String categoryId) {
//		categoryService.deleteCategory(categoryId);
//		return ApiResponse.<String>builder().result("Category has been deleted").build();
//	}
//
//	@PutMapping("/{categoryId}")
//	ApiResponse<Category> updateUser(@PathVariable String categoryId, @Valid @RequestBody CategoryUpdateRequest request) {
//		return ApiResponse.<Category>builder().result(categoryService.updateCategory(categoryId, request)).build();
//	}

}

//@RestController
//@RequestMapping("/categories")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class CategoryController {
//	CategoryService categoryService;
//
//	@GetMapping
//	ApiResponse<PagedResponse<Category>> getCategories(
//			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
//			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
//			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {
//
//		PagedResponse<Category> pagedResponse = categoryService.getCategories(page, limit, sort, search);
//
//		return ApiResponse.<PagedResponse<Category>>builder().result(pagedResponse).build();
//	}
//
//	@PostMapping
//	ApiResponse<Category> createCategory(@Valid @RequestBody CategoryCreationRequest request) {
//		return ApiResponse.<Category>builder().result(categoryService.createCategory(request)).build();
//	}
//
//	@DeleteMapping("/{categoryId}")
//	ApiResponse<String> deleteCategory(@PathVariable String categoryId) {
//		categoryService.deleteCategory(categoryId);
//		return ApiResponse.<String>builder().result("Category has been deleted").build();
//	}
//
//	@PutMapping("/{categoryId}")
//	ApiResponse<Category> updateUser(@PathVariable String categoryId, @Valid @RequestBody CategoryUpdateRequest request) {
//		return ApiResponse.<Category>builder().result(categoryService.updateCategory(categoryId, request)).build();
//	}
//	
//}
