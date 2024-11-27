package com.backend.mapper;

import org.mapstruct.Mapper;

import com.backend.dto.request.delivery.DeliveryCreationRequest;
import com.backend.dto.request.order.delivery.DeliveryRequest;
import com.backend.entity.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
	Delivery toDelivery(DeliveryCreationRequest request);
	Delivery toDelivery(DeliveryRequest request);
}
