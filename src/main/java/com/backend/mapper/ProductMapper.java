package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.UserResponse;
import com.backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
     Product toProduct(ProductCreationRequest request);

}
