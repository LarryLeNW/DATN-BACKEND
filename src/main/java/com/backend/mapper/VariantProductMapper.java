package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.dto.request.variantProduct.VariantProductCreationRequest;
import com.backend.dto.request.variantProduct.VariantProductUpdateRequest;
import com.backend.dto.response.variantProduct.VariantProductResponse;
import com.backend.entity.VariantProduct;

@Mapper(componentModel = "spring")
public interface VariantProductMapper {

	// Chuyển đổi đối tượng VariantProductCreationRequest thành đối tượng
	// VariantProduct
	VariantProduct toVariantProduct(VariantProductCreationRequest request);

	// Chuyển đổi đối tượng VariantProduct thành đối tượng VariantProductResponse
	VariantProductResponse toVariantProductResponse(VariantProduct variantProduct);

	//ko cập nhật thuộc tính ở dưới
	@Mapping(target = "product", ignore = true) 
	@Mapping(target = "attributes", ignore = true)
	void updateVariantProduct(@MappingTarget VariantProduct variantProduct, VariantProductUpdateRequest request);
}
