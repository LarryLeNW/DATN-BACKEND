package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.AttributeProduct;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ProductMapper;
import com.backend.repository.AttributeProductRepository;
import com.backend.repository.BrandRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.utils.Helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ProductService {

	final CategoryRepository categoryRepository;
	final BrandRepository brandRepository;
	final ProductRepository productRepository;
	final AttributeProductRepository attributeProductRepository;
	final ProductMapper productMapper;
	final EntityManager entityManager;

	// Paginated retrieval of products
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

		Product productCreated = productRepository.save(product);

		if (productCreated != null) {
			List<AttributeProduct> attributes = request.getAttributes().stream().map(attrRequest -> {
				boolean exists = attributeProductRepository.existsByNameAndProductId(attrRequest.getName(),
						productCreated.getId());
				if (!exists) {
					return new AttributeProduct(attrRequest.getName(), attrRequest.getValue(), productCreated);
				} else {
					return null;
				}
			}).filter(attribute -> attribute != null).collect(Collectors.toList());

			if (!attributes.isEmpty()) {
				attributeProductRepository.saveAll(attributes);
			}
		}

		// Return the product response
		return productMapper.toProductResponse(productCreated);
	}

	// Update product
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

	// Delete product
	public void deleteProduct(String productId) {
		productRepository.deleteById(productId);
	}
}
