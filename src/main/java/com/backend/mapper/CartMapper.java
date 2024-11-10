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
import com.backend.entity.Sku;


@Mapper(componentModel = "spring")
public interface CartMapper {

//    List<OrderDetailResponse> toOrderDetailResponseList(List<OrderDetail> orderDetails);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
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
}
