package com.backend.mapper;

import java.util.HashMap;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.Product;

@Component
public class ProductMapper {

	public ProductResponse toDTO(Product product) {
		ProductResponse productDTO = new ProductResponse();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setSlug(product.getSlug());
		productDTO.setSlug(product.getSlug());
		productDTO.setDescription(product.getDescription());
//		productDTO.setCategory(product.getCategory() != null ? product.getCategory() : null);
//		productDTO.setBrand(product.getBrand() != null ? product.getBrand() : null);
		productDTO.setCreatedAt(product.getCreatedAt());
		productDTO.setUpdatedAt(product.getUpdatedAt());

		List<ProductResponse.SKUDTO> skuDTOs = product.getSkus().stream().map(sku -> {
			ProductResponse.SKUDTO skuDTO = new ProductResponse.SKUDTO();
			skuDTO.setPrice(sku.getPrice());
			skuDTO.setPrice(sku.getPrice());
			skuDTO.setStock(sku.getStock());
			skuDTO.setDiscount(sku.getDiscount());
			skuDTO.setCode(sku.getCode());
			skuDTO.setId(sku.getId());
			skuDTO.setImages(sku.getImages());

			skuDTO.setAttributes(sku.getAttributeOptionSkus().stream()
					.collect(Collectors.toMap(
							attributeOptionSku -> attributeOptionSku.getAttributeOption().getAttribute().getName(),
							attributeOptionSku -> attributeOptionSku.getAttributeOption().getValue(),
							(existing, replacement) -> existing, 
							HashMap::new 
			)));

			return skuDTO;
		}).collect(Collectors.toList());

		productDTO.setSkus(skuDTOs);
		return productDTO;
	}

}