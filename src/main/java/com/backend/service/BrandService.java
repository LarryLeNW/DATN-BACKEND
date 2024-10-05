//package com.backend.service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//
//import com.backend.dto.request.brand.BrandCreationRequest;
//import com.backend.dto.request.brand.BrandUpdateRequest;
//import com.backend.dto.request.category.CategoryCreationRequest;
//import com.backend.dto.request.category.CategoryUpdateRequest;
//import com.backend.dto.response.common.PagedResponse;
//import com.backend.entity.Brand;
//import com.backend.entity.Category;
//import com.backend.exception.AppException;
//import com.backend.exception.ErrorCode;
//import com.backend.mapper.BrandMapper;
//import com.backend.repository.BrandRepository;
//import com.backend.repository.common.CustomSearchRepository;
//import com.backend.repository.common.SearchType;
//import com.backend.utils.Helpers;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.criteria.CriteriaQuery;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class BrandService {
//	
//	BrandRepository brandRepository;
//	BrandMapper brandMapper;
//	EntityManager entityManager; 
//	
//	
//	public PagedResponse<Brand> getBrands(int page, int limit, String sort, String... search) {
//		List<SearchType> criteriaList = new ArrayList<>();
//		CustomSearchRepository<Brand> customSearchService = new CustomSearchRepository<>(entityManager);
//
//		CriteriaQuery<Brand> query = customSearchService.buildSearchQuery(Brand.class, search, sort);
//
//		List<Brand> brands = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
//				.setMaxResults(limit).getResultList();
//
//		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Brand.class, search);
//		long totalElements = entityManager.createQuery(countQuery).getSingleResult();
//
//		int totalPages = (int) Math.ceil((double) totalElements / limit);
//
//		return new PagedResponse<>(brands, page, totalPages, totalElements, limit);
//	}
//
//	public Brand createBrand(BrandCreationRequest request) {
//		Brand brand = brandMapper.toBrand(request);
//		brandRepository.save(brand);
//		return brand;
//	}
//	
//	public Brand updateBrand(String brandId, BrandUpdateRequest request) {
//		Brand brand = brandRepository.findById(brandId)
//				.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
//
//		Helpers.updateEntityFields(request, brand); 
//		return brandRepository.save(brand);
//	}
//	
//	public void deleteBrand(String brandId) {
//		brandRepository.deleteById(brandId);
//	}
//
//
//}
