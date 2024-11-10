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
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Sku;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toOrderRequest(OrderCreationRequest request);

    @Mapping(source = "delivery", target = "delivery")
    @Mapping(source = "orderDetails", target = "orderDetails")
    OrderResponse toOrderResponse(Order order);

    List<OrderDetailResponse> toOrderDetailResponseList(List<OrderDetail> orderDetails);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "sku", target = "sku") 
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

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

        // Map các thuộc tính từ attributeOptionSkus sang Map trong SkuDTO
        skuDTO.setAttributes(
            sku.getAttributeOptionSkus().stream()
                .collect(Collectors.toMap(
                    attributeOptionSku -> attributeOptionSku.getAttributeOption().getAttribute().getName(),
                    attributeOptionSku -> attributeOptionSku.getAttributeOption().getValue(),
                    (existing, replacement) -> existing, // xử lý trường hợp trùng lặp key
                    HashMap::new
                ))
        );

        return skuDTO;
    }
}
