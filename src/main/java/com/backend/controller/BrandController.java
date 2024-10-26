package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.brand.BrandCreationRequest;

import com.backend.dto.response.ApiResponse;

import com.backend.dto.request.brand.BrandUpdateRequest;
import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;

import com.backend.dto.response.common.PagedResponse;

import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.service.BrandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BrandController {

	BrandService brandService;
	
	@Autowired
	ObjectMapper objectMapper; 

	@GetMapping
	ApiResponse<PagedResponse<Brand>> getAll(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {

		PagedResponse<Brand> pagedResponse = brandService.getBrands(page, limit, sort, search);

		return ApiResponse.<PagedResponse<Brand>>builder().result(pagedResponse).build();
	}

	@PostMapping
	ApiResponse<Brand> create(@RequestParam String brandData , @RequestParam(required = false) MultipartFile image) throws JsonMappingException, JsonProcessingException {
		BrandCreationRequest brandRequest = objectMapper.readValue(brandData, BrandCreationRequest.class);
		return ApiResponse.<Brand>builder().result(brandService.createBrand(brandRequest , image)).build();
	}

	@DeleteMapping("/{brandId}")
	ApiResponse<String> delete(@PathVariable Long brandId) {
		brandService.deleteBrand(brandId);
		return ApiResponse.<String>builder().result("Brand has been deleted").build();
	}

	@PutMapping("/{brandId}")
	ApiResponse<Brand> update(@PathVariable Long brandId, @RequestParam(required = false) String brandData , @RequestParam(required = false) MultipartFile image ) throws JsonMappingException, JsonProcessingException {
			
		BrandUpdateRequest brandRequest = null;
		
		if(brandData != null)
			 brandRequest = objectMapper.readValue(brandData, BrandUpdateRequest.class);
		
		return ApiResponse.<Brand>builder().result(brandService.updateBrand(brandId, brandRequest, image)).build();
	}
	
}
