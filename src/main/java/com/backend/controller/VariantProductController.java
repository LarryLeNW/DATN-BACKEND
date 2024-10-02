package com.backend.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.variantProduct.VariantProductCreationRequest;
import com.backend.dto.request.variantProduct.VariantProductUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.variantProduct.VariantProductResponse;
import com.backend.entity.VariantProduct;
import com.backend.service.VariantProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/variantProduct")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Validated
public class VariantProductController {
	
      VariantProductService variantProductService;
      
  	@GetMapping
  	public ApiResponse<PagedResponse<VariantProduct>> getAllProducts(
  			@RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
  			@RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
  			@RequestParam(required = false) String sort, @RequestParam(required = false) String[] search) {

  		PagedResponse<VariantProduct> pagedResponse = variantProductService.getVariantProduct(page, limit, sort, search);

  		return ApiResponse.<PagedResponse<VariantProduct>>builder().result(pagedResponse).build();
  	}
  	
  	@PostMapping
  	public ApiResponse<VariantProductResponse> createVariantProduct(@Valid @RequestBody VariantProductCreationRequest request){
  		return ApiResponse.<VariantProductResponse>builder().result(variantProductService.createVariantProduct(request)).build();

  	}
  	@PutMapping("{variantProductId}")
  	public ApiResponse<VariantProductResponse> updateVariantProduct(@PathVariable String variantProductId, @Valid @RequestBody VariantProductUpdateRequest request){
  		return ApiResponse.<VariantProductResponse>builder().result(variantProductService.updateVariantProduct(request,variantProductId)).build();
  	}
  	
  	@DeleteMapping("{variantProductId}")
  	ApiResponse<String> deleteVariantProduct(@PathVariable String variantProductId) { 
		variantProductService.deleteVariantProduct(variantProductId);
		return ApiResponse.<String>builder().result("VariantProduct has been deleted").build();
	}


	
}
