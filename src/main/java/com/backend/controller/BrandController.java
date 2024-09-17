package com.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.entity.Brand;
import com.backend.service.BrandService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/brand")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BrandController {

	BrandService brandService;

	@GetMapping
	ApiResponse<List<Brand>> getBrands() {
		return ApiResponse.<List<Brand>>builder().result(brandService.getBrand()).build();
	}

	@PostMapping
	ApiResponse<Brand> createBrand(@Valid @RequestBody BrandCreationRequest request) {
		return ApiResponse.<Brand>builder().result(brandService.createBrand(request)).build();
	}
}
