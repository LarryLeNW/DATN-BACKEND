package com.backend.mapper;

import org.mapstruct.Mapper;

import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.Category;

@Mapper(componentModel = "spring")

public interface CategoryMapper {
	Category toCategory(CategoryCreationRequest request);
	

}
