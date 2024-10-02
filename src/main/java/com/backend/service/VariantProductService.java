package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.dto.request.variantProduct.VariantProductCreationRequest;
import com.backend.dto.request.variantProduct.VariantProductUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.variantProduct.VariantProductResponse;
import com.backend.entity.AttributeProduct;
import com.backend.entity.Product;
import com.backend.entity.VariantProduct;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.VariantProductMapper;
import com.backend.repository.AttributeProductRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.VariantProductRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.utils.Helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class VariantProductService {

	final EntityManager entityManager;
	final VariantProductMapper variantProductMapper;
	final VariantProductRepository variantProductRepository;
	final ProductRepository productRepository;
	final AttributeProductRepository attributeProductRepository;


	public PagedResponse<VariantProduct> getVariantProduct(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<VariantProduct> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<VariantProduct> query = customSearchService.buildSearchQuery(VariantProduct.class, search, sort);

		List<VariantProduct> variantProducts = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
				.setMaxResults(limit).getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(VariantProduct.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(variantProducts, page, totalPages, totalElements, limit);
	}

	public VariantProductResponse createVariantProduct(VariantProductCreationRequest request) {
	    VariantProduct variantProduct = variantProductMapper.toVariantProduct(request);
	    
	    Product product = productRepository.findById(request.getProductId())
	            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
	    
	    variantProduct.setProduct(product);
	    
	    VariantProduct createVariantProduct = variantProductRepository.save(variantProduct);

//	    if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
//	        List<AttributeProduct> newAttributes = request.getAttributes().stream()
//	                .map(attr -> new AttributeProduct(attr.getName(), attr.getValue()))
//	                .collect(Collectors.toList());
//
//	        attributeProductRepository.saveAll(newAttributes);
//
//	        createVariantProduct.setAttributes(newAttributes);
//	    }

	    variantProductRepository.save(createVariantProduct);

	    return variantProductMapper.toVariantProductResponse(createVariantProduct);
	}



	public VariantProductResponse updateVariantProduct(VariantProductUpdateRequest request, String variantProductId) {

	    // Tìm vriantProduct để cập nhật
	    VariantProduct variantProduct = variantProductRepository.findById(variantProductId)
	        .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_EXISTED));
	    
	    List<AttributeProduct> updateAttributes = request.getAttributes();

	    // Cập nhật products nếu thay đổi
	    if (request.getProductId() != null) {
	        Product product = productRepository.findById(request.getProductId())
	            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
	        variantProduct.setProduct(product);
	    }

	
	    Helpers.updateFieldEntityIfChanged(request.getName(), variantProduct.getName(), variantProduct::setName);
	    Helpers.updateFieldEntityIfChanged(request.getDescription(), variantProduct.getDescription(), variantProduct::setDescription);
	    Helpers.updateFieldEntityIfChanged(request.getPrice(), variantProduct.getPrice(), variantProduct::setPrice);
	    Helpers.updateFieldEntityIfChanged(request.getStock(), variantProduct.getStock(), variantProduct::setStock);
	    Helpers.updateFieldEntityIfChanged(request.getThumbnail_url(), variantProduct.getThumbnail_url(), variantProduct::setThumbnail_url);

	   
	    if (variantProduct.getAttributes() != null) {
	        for (AttributeProduct att : variantProduct.getAttributes()) {
	            attributeProductRepository.deleteById(att.getId());
	        }
	    }
	    
	    // Cập nhật thuộc tính mới
	    if (updateAttributes != null && !updateAttributes.isEmpty()) {
	        List<AttributeProduct> newAttributes = updateAttributes.stream()
	            .map(attr -> new AttributeProduct(attr.getName(), attr.getValue()))
	            .collect(Collectors.toList());
	        variantProduct.setAttributes(newAttributes);
	    }

	    VariantProduct updatedVariantProduct = variantProductRepository.save(variantProduct);

	    
	    return variantProductMapper.toVariantProductResponse(updatedVariantProduct);
	}
	
	public void deleteVariantProduct(String variantProductId) {
		variantProductRepository.deleteById(variantProductId);
	}


}
