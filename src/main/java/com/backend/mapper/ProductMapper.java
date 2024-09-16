package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	Product toProduct(ProductCreationRequest request);

	ProductResponse toProductResponse(Product product);

	@Mapping(target = "category", ignore = true)
	@Mapping(target = "brand", ignore = true)
	void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);
}
