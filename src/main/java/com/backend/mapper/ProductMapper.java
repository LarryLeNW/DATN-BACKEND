package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;
import com.backend.dto.response.product.ProductResponse.BrandDTO;
import com.backend.dto.response.product.ProductResponse.CategoryDTO;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.entity.Sku;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "skus", target = "skus", qualifiedByName = "toSkuDTOs")
    ProductResponse toDTO(Product product);

    default CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        return new CategoryDTO(category.getId(), category.getName());
    }
    
    default BrandDTO toBrandDTO(Brand brand) {
        if (brand == null) return null;
        return new BrandDTO(brand.getId(), brand.getName());
    }

    @Named("toSkuDTOs")
    default List<SKUDTO> toSkuDTOs(List<Sku> skus) {
        if (skus == null || skus.isEmpty()) return List.of();
        return skus.stream()
                .map(this::toSkuDTO)
                .collect(Collectors.toList());
    }

    default SKUDTO toSkuDTO(Sku sku) {
        if (sku == null) return null;

        SKUDTO skuDTO = new SKUDTO();
        skuDTO.setId(sku.getId());
        skuDTO.setCode(sku.getCode());
        skuDTO.setPrice(sku.getPrice());
        skuDTO.setStock(sku.getStock());
        skuDTO.setDiscount(sku.getDiscount());
        skuDTO.setImages(sku.getImages());

        skuDTO.setAttributes(
                sku.getAttributeOptionSkus().stream()
                        .collect(Collectors.toMap(
                                attributeOptionSku -> attributeOptionSku.getAttributeOption().getAttribute().getName(),
                                attributeOptionSku -> attributeOptionSku.getAttributeOption().getValue(),
                                (existing, replacement) -> existing, 
                                HashMap::new 
                        ))
        );

        return skuDTO;
    }
}
