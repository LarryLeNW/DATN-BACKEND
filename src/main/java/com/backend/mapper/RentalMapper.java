package com.backend.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import java.util.HashMap;

import java.util.List;
import java.util.stream.Collectors;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.rental.DetailRentalCreation;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.order.PaymentResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;
import com.backend.dto.response.rental.RentailDetailReponse;
import com.backend.dto.response.rental.RentalResponse;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Payment;
import com.backend.entity.Sku;
import com.backend.entity.rental.Rental;
import com.backend.entity.rental.RentalDetail;
import com.backend.repository.rental.RentalDetailRepository;

@Mapper(componentModel = "spring")
public interface RentalMapper {

	@Mapping(source = "delivery", target = "delivery")
	@Mapping(source = "rentalDetails", target = "rentalDetails")
	@Mapping(source = "user", target = "user")
	@Mapping(source = "payment", target = "payment")
	RentalResponse toRentalResponse(Rental rental);

	List<RentailDetailReponse> toRentailDetailReponseList(List<RentalDetail> rentalDetails);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "product.id", target = "productId")
	@Mapping(source = "product.name", target = "productName")
	@Mapping(source = "rental.id", target = "rentalId")
	@Mapping(source = "sku", target = "sku")
	@Mapping(source = "quantity", target = "quantity")
	RentailDetailReponse toRENDetailReponse(RentalDetail rentalDetail);

	default SKUDTO toSkuDTO(Sku sku) {
		if (sku == null)
			return null;

		SKUDTO skuDTO = new SKUDTO();
		skuDTO.setId(sku.getId());
		skuDTO.setCode(sku.getCode());
		skuDTO.setPrice(sku.getPrice());
		skuDTO.setStock(sku.getStock());
		skuDTO.setDiscount(sku.getDiscount());
		skuDTO.setImages(sku.getImages());
		skuDTO.setCanBeRented(sku.getCanBeRented());
		skuDTO.setHourlyRentPrice(sku.getHourlyRentPrice());
		skuDTO.setDailyRentPrice(sku.getDailyRentPrice());
		skuDTO.setMinRentalQuantity(sku.getMinRentalQuantity());
		skuDTO.setMaxRentalQuantity(sku.getMaxRentalQuantity());

		skuDTO.setAttributes(sku.getAttributeOptionSkus().stream()
				.collect(Collectors.toMap(
						attributeOptionSku -> attributeOptionSku.getAttributeOption().getAttribute().getName(),
						attributeOptionSku -> attributeOptionSku.getAttributeOption().getValue(),
						(existing, replacement) -> existing, HashMap::new)));

		return skuDTO;
	}

	@Mapping(source = "id", target = "id")
	@Mapping(source = "amount", target = "amount")
	@Mapping(source = "method", target = "method")
	@Mapping(source = "status", target = "status")
	PaymentResponse toPaymentResponse(Payment payment);

}
