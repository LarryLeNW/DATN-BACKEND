package com.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.CategoryMapper;
import com.backend.mapper.ProductMapper;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
	CategoryRepository categoryRepository;
	CategoryMapper categoryMapper;
	EntityManager entityManager;
	
	
	// get all categories
	public PagedResponse<Category> getCategories(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Category> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Category> query = customSearchService.buildSearchQuery(Category.class, search, sort);

		List<Category> categories = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
				.setMaxResults(limit).getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Category.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(categories, page, totalPages, totalElements, limit);
	}

	public Category createCategory(CategoryCreationRequest request) {
		Category category = categoryMapper.toCategory(request);
		categoryRepository.save(category);
		return category;
	}
	
	public Category updateCategory(String categoryId, CategoryUpdateRequest request) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

		Helpers.updateEntityFields(request, category); 

		return categoryRepository.save(category);
	}
	
	public void deleteCategory(String categoryId) {
		categoryRepository.deleteById(categoryId);
	}

}
