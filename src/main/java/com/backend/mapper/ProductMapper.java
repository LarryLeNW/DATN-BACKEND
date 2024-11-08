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
		productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
		productDTO.setBrandId(product.getBrand() != null ? product.getBrand().getId() : null);
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
							(existing, replacement) -> existing, // xử lý trường hợp trùng lặp key, bạn có thể giữ lại
																	// giá trị cũ
							HashMap::new // chỉ định loại bản đồ là HashMap<String, String>
			)));

			return skuDTO;
		}).collect(Collectors.toList());

		productDTO.setSkus(skuDTOs);
		return productDTO;
	}

}