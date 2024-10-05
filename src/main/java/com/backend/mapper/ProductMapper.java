package com.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backend.dto.request.product.ProductDTO;
import com.backend.entity.Product;


@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());

//        List<SkuDTO> skus = product.getSkus().stream().map(this:).collect(Collectors.toList());

        return dto;
    }

  
}