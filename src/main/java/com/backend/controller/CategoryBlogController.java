package com.backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.categoryBlog.CategoryBlogCreationRequest;
import com.backend.dto.request.categoryBlog.CategoryBlogUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Category;
import com.backend.entity.CategoryBlog;
import com.backend.service.BrandService;
import com.backend.service.CategoryBlogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categoryBlog")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryBlogController {
	
	CategoryBlogService categoryBlogService;

	
	@GetMapping
	public ApiResponse<PagedResponse<CategoryBlog>> getCategoryBlog (
		@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
		@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
		@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {
		
		PagedResponse<CategoryBlog> pagedResponse = categoryBlogService.getCategoryBlog(page, limit, sort, search);
		
		return ApiResponse.<PagedResponse<CategoryBlog>>builder().result(pagedResponse).build();
			
	}
	@PostMapping
	public ApiResponse<CategoryBlog> createCategoryBlog (@Valid @RequestBody CategoryBlogCreationRequest request){
		return ApiResponse.<CategoryBlog>builder().result(categoryBlogService.createCategoryBlog(request)).build();
	}
	
	@PutMapping("/{categoryBlogId}")
	public ApiResponse<CategoryBlog> updateCategoryBlog(@Valid @RequestBody CategoryBlogUpdateRequest request , @PathVariable Integer categoryBlogId){
		return ApiResponse.<CategoryBlog>builder().result(categoryBlogService.updateCategoryBlog(request, categoryBlogId)).build();
	}
	@DeleteMapping("/{categoryBlogId}")
	public ApiResponse<String> deleteCategoryBlog(@PathVariable Integer categoryBlogId){
		categoryBlogService.deleteCategoryBlog(categoryBlogId);
		return ApiResponse.<String>builder().result("delete success categoryblog with id of: "+categoryBlogId).build();
	}
}
