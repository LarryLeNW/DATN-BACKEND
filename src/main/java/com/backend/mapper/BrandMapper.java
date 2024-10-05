package com.backend.mapper;

import org.mapstruct.Mapper;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.entity.Brand;


@Mapper(componentModel = "spring")
public interface BrandMapper {

	Brand toBrand(BrandCreationRequest request);

}
