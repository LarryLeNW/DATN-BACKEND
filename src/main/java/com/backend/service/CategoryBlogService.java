package com.backend.service;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.request.categoryBlog.CategoryBlogCreationRequest;
import com.backend.dto.request.categoryBlog.CategoryBlogUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Category;
import com.backend.entity.CategoryBlog;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.CategoryBlogMapper;
import com.backend.repository.CategoryBlogRepository;
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
public class CategoryBlogService {

	CategoryBlogRepository categoryBlogRepository;

	CategoryBlogMapper categoryBlogMapper;

	EntityManager entityManager;

	public CategoryBlog createCategoryBlog(CategoryBlogCreationRequest request) {
		CategoryBlog categoryBlog = categoryBlogMapper.toCategoryBlog(request);
		
		return categoryBlogRepository.save(categoryBlog);
	}

	public CategoryBlog updateCategoryBlog(CategoryBlogUpdateRequest request, Integer categoryBlogId) {

		CategoryBlog categoryBlog = categoryBlogRepository.findById(categoryBlogId)
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORYBLOG_NOT_EXISTED));
		if (request != null) {
			Helpers.updateFieldEntityIfChanged(request.getName(), categoryBlog.getName(), categoryBlog::setName);
		}
		
		return categoryBlogRepository.save(categoryBlog);
	}

	public void deleteCategoryBlog(Integer categoryBlogId) {
		categoryBlogRepository.deleteById(categoryBlogId);
	}

	public PagedResponse<CategoryBlog> getCategoryBlog(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<CategoryBlog> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<CategoryBlog> query = customSearchService.buildSearchQuery(CategoryBlog.class, search, sort);

		List<CategoryBlog> categoryBlog = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
				.setMaxResults(limit).getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(CategoryBlog.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(categoryBlog, page, totalPages, totalElements, limit);
	}

}
