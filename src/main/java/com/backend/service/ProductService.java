//package com.backend.service;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//
//import com.backend.dto.request.product.ProductCreationRequest;
//import com.backend.dto.request.product.ProductUpdateRequest;
//import com.backend.dto.response.common.PagedResponse;
//import com.backend.dto.response.product.ProductResponse;
//import com.backend.entity.AttributeProduct;
//import com.backend.entity.Brand;
//import com.backend.entity.Category;
//import com.backend.entity.Product;
//import com.backend.exception.AppException;
//import com.backend.exception.ErrorCode;
//import com.backend.mapper.ProductMapper;
//import com.backend.repository.AttributeProductRepository;
//import com.backend.repository.BrandRepository;
//import com.backend.repository.CategoryRepository;
//import com.backend.repository.ProductRepository;
//import com.backend.repository.common.CustomSearchRepository;
//import com.backend.repository.common.SearchType;
//import com.backend.repository.product.ProductQueryRepository;
//import com.backend.utils.Helpers;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.transaction.Transactional;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Slf4j
//public class ProductService {
//
//	final CategoryRepository categoryRepository;
//	final BrandRepository brandRepository;
//	final ProductRepository productRepository;
//	final AttributeProductRepository attributeProductRepository;
//	final ProductMapper productMapper;
//	final EntityManager entityManager;
//
//	public PagedResponse<Product> getProducts(int page, int limit, String sort, String... search ) {
//		List<SearchType> criteriaList = new ArrayList<>();
//		ProductQueryRepository<Product> customSearchService = new ProductQueryRepository<>(entityManager);
//
//		CriteriaQuery<Product> query = customSearchService.buildSearchQuery(Product.class, search, sort);
//
//		List<Product> products = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
//				.setMaxResults(limit).getResultList();
//
//		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Product.class, search);
//		long totalElements = entityManager.createQuery(countQuery).getSingleResult();
//
//		int totalPages = (int) Math.ceil((double) totalElements / limit);
//
//		return new PagedResponse<>(products, page, totalPages, totalElements, limit);
//	}
//
//	public ProductResponse createProduct(ProductCreationRequest request) {
//		Product product = productMapper.toProduct(request);
//
//		Category category = categoryRepository.findById(request.getCategoryId())
//				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
//
//		Brand brand = brandRepository.findById(request.getBrandId())
//				.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
//
//		product.setCategory(category);
//		product.setBrand(brand);
//
//		Product productCreated = productRepository.save(product);
//
//		// Thêm lại thuộc tính từ request
//		if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
//			List<AttributeProduct> newAttributes = request.getAttributes().stream()
//					.map(attr -> new AttributeProduct(attr.getName(), attr.getValue())).collect(Collectors.toList());
//
//			// Lưu các thuộc tính mới vào product
//			product.setAttributes(newAttributes);
////	        attributeProductRepository.saveAll(newAttributes);
//		}
//
//		// Return the product response
//		return productMapper.toProductResponse(productCreated);
//	}
//
//	@Transactional
//	public Product updateProduct(String productId, ProductUpdateRequest request) {
//		// Tìm sản phẩm để cập nhật
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
//
//		System.out.println(product.toString());
//
//		List<AttributeProduct> updateAttributes = request.getAttributes();
//		// Cập nhật Brand nếu thay đổi
//		if (request.getBrandId() != null && !request.getBrandId().equals(product.getBrand().getId())) {
//			Brand brand = brandRepository.findById(request.getBrandId())
//					.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
//			product.setBrand(brand);
//		}
//
//		// Cập nhật Category nếu thay đổi
//		if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategory().getId())) {
//			Category category = categoryRepository.findById(request.getCategoryId())
//					.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
//			product.setCategory(category);
//		}
//
//		Helpers.updateFieldEntityIfChanged(request.getName(), product.getName(), product::setName);
//		Helpers.updateFieldEntityIfChanged(request.getDescription(), product.getDescription(), product::setDescription);
//		Helpers.updateFieldEntityIfChanged(request.getPrice(), product.getPrice(), product::setPrice);
//		Helpers.updateFieldEntityIfChanged(request.getStock(), product.getStock(), product::setStock);
//		Helpers.updateFieldEntityIfChanged(request.getThumbnail_url(), product.getThumbnail_url(), product::setThumbnail_url);
//
//
//		if (product.getAttributes() != null) {
//			for (AttributeProduct att : product.getAttributes()) {
//				attributeProductRepository.deleteById(att.getId());
//			}
//		}
//
//		if (updateAttributes != null && !updateAttributes.isEmpty()) {
//			List<AttributeProduct> newAttributes = updateAttributes.stream()
//					.map(attr -> new AttributeProduct(attr.getName(), attr.getValue())).collect(Collectors.toList());
//			// Lưu các thuộc tính mới vào product
//			product.setAttributes(newAttributes);
////	        attributeProductRepository.saveAll(newAttributes);
//		}
//
//		// Lưu sản phẩm đã cập nhật
//		Product updatedProduct = productRepository.save(product);
//
//		System.out.println("èdsaf");
//
//		// Trả về ProductResponse
//		return updatedProduct;
//	}
//
//	// Delete product
//	public void deleteProduct(String productId) {
//		productRepository.deleteById(productId);
//	}
//}
