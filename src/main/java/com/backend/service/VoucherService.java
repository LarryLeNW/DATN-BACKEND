package com.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.dto.request.brand.BrandUpdateRequest;
import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;
import com.backend.dto.request.voucher.VoucherCreationRequest;
import com.backend.dto.request.voucher.VoucherUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.dto.response.voucher.VoucherResponse;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.User;
import com.backend.entity.Voucher;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BrandMapper;
import com.backend.mapper.UserMapper;
import com.backend.mapper.VoucherMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.product.VoucherRepository;
import com.backend.repository.user.UserRepository;
import com.backend.specification.CartSpecification;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;
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

	UserRepository userRepository;

	UserMapper userMapper;

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

		return new PagedResponse<>(voucherResponses, page + 1, voucherPage.getTotalPages(),
				voucherPage.getTotalElements(), limit);
	}

	public PagedResponse<VoucherResponse> getVoucherByCustomer(Map<String, String> params) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userId = auth.getName();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);

	    Specification<Voucher> spec = (root, query, builder) -> builder.and(
	        builder.isFalse(root.get("isDestroy")), 
	        builder.or(
	            builder.isTrue(root.get("isPublic")), 
	            builder.isMember(user, root.get("users")) 
	        ),
	        builder.greaterThan(root.get("expiry_date"), LocalDateTime.now())
	    );

		
		if (params.containsKey("keyword")) {
			String keyword = params.get("keyword").toLowerCase();
			spec = spec.and((root, query, builder) -> builder.or(
					builder.like(builder.lower(root.get("code")), "%" + keyword + "%"),
					builder.like(builder.lower(root.get("name")), "%" + keyword + "%")));
		}

		Page<Voucher> voucherPage = voucherRepository.findAll(spec, pageable);
		List<VoucherResponse> voucherResponses = voucherPage.getContent().stream().map(voucherMapper::toVoucherResponse)
				.collect(Collectors.toList());

		return new PagedResponse<>(voucherResponses, page + 1, voucherPage.getTotalPages(),
				voucherPage.getTotalElements(), limit);
	}

	public Voucher create(VoucherCreationRequest request) {
		Voucher voucher = voucherMapper.toVoucher(request);
		return voucherRepository.save(voucher);
	}

	public Voucher update(Long voucherId, VoucherUpdateRequest request) throws BadRequestException {
		Voucher voucherFound = voucherRepository.findById(voucherId)
				.orElseThrow(() -> new BadRequestException("Not found voucher ..."));

		if (request != null) {
			Helpers.updateFieldEntityIfChanged(request.getName(), voucherFound.getName(), voucherFound::setName);
			Helpers.updateFieldEntityIfChanged(request.getCode(), voucherFound.getCode(), voucherFound::setCode);
			Helpers.updateFieldEntityIfChanged(request.getDiscount_type(), voucherFound.getDiscount_type(),
					voucherFound::setDiscount_type);
			Helpers.updateFieldEntityIfChanged(request.getExpiry_date(), voucherFound.getExpiry_date(),
					voucherFound::setExpiry_date);
			Helpers.updateFieldEntityIfChanged(request.getIsDestroy(), voucherFound.getIsDestroy(),
					voucherFound::setIsDestroy);
			Helpers.updateFieldEntityIfChanged(request.getIsPublic(), voucherFound.getIsPublic(),
					voucherFound::setIsPublic);
			Helpers.updateFieldEntityIfChanged(request.getMax_discount(), voucherFound.getMax_discount(),
					voucherFound::setMax_discount);
			Helpers.updateFieldEntityIfChanged(request.getMin_order(), voucherFound.getMin_order(),
					voucherFound::setMin_order);
			Helpers.updateFieldEntityIfChanged(request.getProducts(), voucherFound.getProducts(),
					voucherFound::setProducts);
			Helpers.updateFieldEntityIfChanged(request.getStart_date(), voucherFound.getStart_date(),
					voucherFound::setStart_date);
			Helpers.updateFieldEntityIfChanged(request.getValue(), voucherFound.getValue(), voucherFound::setValue);
		}

		return voucherRepository.save(voucherFound);
	}

	public void delete(Long voucherId) {
		voucherRepository.deleteById(voucherId);
	}


	public PagedResponse<VoucherResponse> saveVoucherByUser(String voucherCode) throws BadRequestException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userId = auth.getName();

		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		Voucher voucherFound = voucherRepository.findOneByCode(voucherCode)
				.orElseThrow(() -> new RuntimeException("Voucher not found"));

		if (!user.getVouchers().contains(voucherFound)) {
			log.info("Adding voucher to user: " + voucherFound.getCode());
			user.getVouchers().add(voucherFound);
			userRepository.save(user);
		}

		log.info("Returning vouchers for user");
		return this.getAll(new HashMap<>());
	}

}
