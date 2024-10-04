//package com.backend.controller;
//
//import java.util.List;
//
//
//import org.springframework.web.bind.annotation.DeleteMapping;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.backend.dto.request.brand.BrandCreationRequest;
//
//import com.backend.dto.response.ApiResponse;
//
//import com.backend.dto.request.brand.BrandUpdateRequest;
//import com.backend.dto.request.category.CategoryCreationRequest;
//import com.backend.dto.request.category.CategoryUpdateRequest;
//
//import com.backend.dto.response.common.PagedResponse;
//
//import com.backend.entity.Brand;
//import com.backend.entity.Category;
//import com.backend.service.BrandService;
//
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Min;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//
//@RestController
//@RequestMapping("/brands")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class BrandController {
//
//	BrandService brandService;
//
//	@GetMapping
//	ApiResponse<PagedResponse<Brand>> getBrands(
//			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
//			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
//			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {
//
//		PagedResponse<Brand> pagedResponse = brandService.getBrands(page, limit, sort, search);
//
//		return ApiResponse.<PagedResponse<Brand>>builder().result(pagedResponse).build();
//	}
//
//	@PostMapping
//	ApiResponse<Brand> createBrands(@Valid @RequestBody BrandCreationRequest request) {
//		return ApiResponse.<Brand>builder().result(brandService.createBrand(request)).build();
//	}
//
//	@DeleteMapping("/{brandId}")
//	ApiResponse<String> deleteCategory(@PathVariable String brandId) {
//		brandService.deleteBrand(brandId);
//		return ApiResponse.<String>builder().result("Brand has been deleted").build();
//	}
//
//	@PutMapping("/{brandId}")
//	ApiResponse<Brand> updateUser(@PathVariable String brandId, @Valid @RequestBody BrandUpdateRequest request) {
//		return ApiResponse.<Brand>builder().result(brandService.updateBrand(brandId, request)).build();
//	}
//	
//}
