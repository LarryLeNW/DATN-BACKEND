package com.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.entity.Category;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.CategoryMapper;
import com.backend.mapper.ProductMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.ProductRepository;

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
	

	//get all categories
	public List<Category> getCategories() {
		try {
			List<Category> categories = categoryRepository.findAll();
			return categories;
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}
	}
	public Category createCategory(CategoryCreationRequest request) {
		Category category = categoryMapper.toCategory(request);		
		
		if(categoryRepository.existsByName(category.getName())) {
			throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
		}
		
		try {
		    //check for existence of the name
			categoryRepository.save(category);
		} catch (Exception e) {
			// TODO: handle exception
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}
		return category;
		
	}
	

}
