package com.backend.controller;

import java.util.List;
import java.util.Map;

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
import com.backend.dto.request.voucher.VoucherCreationRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.voucher.VoucherResponse;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Voucher;
import com.backend.service.BrandService;
import com.backend.service.VoucherService;
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
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VoucherController {

	VoucherService voucherService;

	@PostMapping
	ApiResponse<Voucher> create(@RequestBody @Valid  VoucherCreationRequest request) {
		return ApiResponse.<Voucher>builder().result(voucherService.create(request)).build();
	}

	@GetMapping
	ApiResponse<PagedResponse<VoucherResponse>> getAlls(@RequestParam Map<String, String> params) {
		return ApiResponse.<PagedResponse<VoucherResponse>>builder().result(voucherService.getAll(params)).build();
	}


}
