package com.backend.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import java.util.HashMap;

import java.util.List;
import java.util.stream.Collectors;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.entity.Cart;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Product;
import com.backend.entity.Sku;


@Mapper(componentModel = "spring")
public interface CartMapper {


    @Mapping(source = "id", target = "id")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product", target = "product")
    @Mapping(source = "sku", target = "sku") 
    CartDetailResponse toCartDetailResponse(Cart cartDetail);

    default SKUDTO toSkuDTO(Sku sku) {
        if (sku == null) {
            return null;
        }
        ProductResponse.SKUDTO skuDTO = new ProductResponse.SKUDTO();
        skuDTO.setPrice(sku.getPrice());
        skuDTO.setStock(sku.getStock());
        skuDTO.setDiscount(sku.getDiscount());
        skuDTO.setCode(sku.getCode());
        skuDTO.setId(sku.getId());
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
    
	default ProductResponse toDTO(Product product) {
		ProductResponse productDTO = new ProductResponse();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setSlug(product.getSlug());
		productDTO.setSlug(product.getSlug());
		productDTO.setDescription(product.getDescription());
		productDTO.setCategory(product.getCategory() != null ? product.getCategory() : null);
		productDTO.setBrand(product.getBrand() != null ? product.getBrand() : null);
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
