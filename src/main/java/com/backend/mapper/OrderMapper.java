package com.backend.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import java.util.HashMap;

import java.util.List;
import java.util.stream.Collectors;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.order.PaymentResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Payment;
import com.backend.entity.Sku;

@Mapper(componentModel = "spring")
public interface OrderMapper {

	Order toOrderRequest(OrderCreationRequest request);

	@Mapping(source = "delivery", target = "delivery")
	@Mapping(source = "orderDetails", target = "orderDetails")
	@Mapping(source = "user", target = "user")
	@Mapping(source = "payment", target = "payment")
	OrderResponse toOrderResponse(Order order);

	List<OrderDetailResponse> toOrderDetailResponseList(List<OrderDetail> orderDetails);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "product.id", target = "productId")
	@Mapping(source = "product.name", target = "productName")
	@Mapping(source = "order.id", target = "orderId")
	@Mapping(source = "sku", target = "sku")
	@Mapping(source = "quantity", target = "quantity")
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

		skuDTO.setAttributes(sku.getAttributeOptionSkus().stream()
				.collect(Collectors.toMap(
						attributeOptionSku -> attributeOptionSku.getAttributeOption().getAttribute().getName(),
						attributeOptionSku -> attributeOptionSku.getAttributeOption().getValue(),
						(existing, replacement) -> existing, 
						HashMap::new)));

		return skuDTO;
	}

	@Mapping(source = "id", target = "id")
	@Mapping(source = "amount", target = "amount")
	@Mapping(source = "method", target = "method")
	@Mapping(source = "status", target = "status")
	PaymentResponse toPaymentResponse(Payment payment);

}
