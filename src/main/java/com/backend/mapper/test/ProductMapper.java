package com.backend.mapper.test;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backend.dto.test.*;
import com.backend.entity.test.*;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());

        List<SkuDTO> skus = product.getSkus().stream().map(this::toSkuDTO1).collect(Collectors.toList());
        dto.setSkus(skus);

        return dto;
    }

    private SkuDTO toSkuDTO1(Sku sku) {
        SkuDTO dto = new SkuDTO();
        dto.setId(sku.getId());
        dto.setCode(sku.getCode());
        dto.setPrice(sku.getPrice());

        // Build attribute string similar to what you provided in the image
        String attributes = sku.getAttributeOptionSkus().stream()
                .map(aos ->  aos.getAttributeOption().getAttribute().getName() + ": " + aos.getAttributeOption().getValue()  )
                .collect(Collectors.joining(", "));
        dto.setAttributes("{" +attributes + "}");

        return dto;
    }

	private <R> R toSkuDTO(Sku sku1) {
		return null;
	}
}