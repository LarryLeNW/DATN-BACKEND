package com.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.dto.request.brand.BrandUpdateRequest;
import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;
import com.backend.dto.request.voucher.VoucherCreationRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.voucher.VoucherResponse;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Voucher;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BrandMapper;
import com.backend.mapper.VoucherMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.product.VoucherRepository;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VoucherService {

	VoucherRepository voucherRepository;

	VoucherMapper voucherMapper;
	
	public PagedResponse<VoucherResponse> getAll(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);

		Specification<Voucher> spec = Specification.where(null);
		if (params.containsKey("keyword")) {
			String keyword = params.get("keyword").toLowerCase();
			spec = spec.and((root, query, builder) -> builder.or(
					builder.like(builder.lower(root.get("code")), "%" + keyword + "%"),
					builder.like(builder.lower(root.get("name")), "%" + keyword + "%")));
		}

		Page<Voucher> voucherPage = voucherRepository.findAll(spec, pageable);
		List<VoucherResponse> voucherResponses = voucherPage.getContent().stream().map(voucherMapper::toVoucherResponse)
				.collect(Collectors.toList());

		return new PagedResponse<>(voucherResponses, page + 1, voucherPage.getTotalPages(), voucherPage.getTotalElements(),
				limit);
	}

	public Voucher create(VoucherCreationRequest request) {
		Voucher voucher = voucherMapper.toVoucher(request);
		return voucherRepository.save(voucher);
	}

//	public Brand updateBrand(Long brandId, BrandUpdateRequest request, MultipartFile image) {
//		Brand brand = brandRepository.findById(brandId)
//				.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
//
//		
//		if(request != null) {
//			Helpers.updateFieldEntityIfChanged(request.getName(), brand.getName(), brand::setName);
//			Helpers.updateFieldEntityIfChanged(request.getDescription(), brand.getDescription(), brand::setDescription);
//
//			if (request.getName() != null)
//				brand.setSlug(Helpers.toSlug(request.getName()));
//		}
//	
//
//		if (image != null) {
//			String imageUrl = uploadFile.saveFile(image, "brandTest");
//			brand.setImage(imageUrl);
//		}
//
//		return brandRepository.save(brand);
//	}
//
//	public void deleteBrand(Long brandId) {
//		brandRepository.deleteById(brandId);
//	}

}
