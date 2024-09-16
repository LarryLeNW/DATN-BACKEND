package com.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ProductMapper;
import com.backend.mapper.UserMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.RoleRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.common.ConsumerCondition;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.utils.Helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.backend.constant.PredefinedRole;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.entity.Role;
import com.backend.entity.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Helper;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

	CategoryRepository categoryRepository;
	BrandRepository brandRepository;
	ProductRepository productRepository;
	ProductMapper productMapper;
	EntityManager entityManager;

	public PagedResponse<Product> getProducts(int page, int limit, String sort, String... search) {

		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Product> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Product> query = customSearchService.buildSearchQuery(Product.class, search, sort);

		List<Product> products = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
				.setMaxResults(limit).getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Product.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(products, page, totalPages, totalElements, limit);
	}

	public ProductResponse createProduct(ProductCreationRequest request) {
		Product product = productMapper.toProduct(request);

		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

		Brand brand = brandRepository.findById(request.getBrandId())
				.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

		product.setCategory(category);
		product.setBrand(brand);
		try {
			product = productRepository.save(product);
		} catch (DataIntegrityViolationException exception) {
			throw new AppException(ErrorCode.PRODUCT_EXISTED);
		}

		return productMapper.toProductResponse(product);
	}

	public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

		Helpers.updateEntityFields(request, product); 

		if (request.getBrandId() != null && !request.getBrandId().equals(product.getBrand().getId())) {
			var brand = brandRepository.findById(request.getBrandId())
					.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
			product.setBrand(brand);
		}

		if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategory().getId())) {
			var category = categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
			product.setCategory(category);
		}

		return productMapper.toProductResponse(productRepository.save(product));
	}

	public void deleteUser(String productId) {
		productRepository.deleteById(productId);
	}

}
