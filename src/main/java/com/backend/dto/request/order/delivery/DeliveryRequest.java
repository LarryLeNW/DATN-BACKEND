package com.backend.dto.request.order.delivery;

import java.util.List;

import com.backend.constant.Type.OrderStatusType;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderDetailCreationRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryRequest {

	String username;
	String address;
	String email;
	String numberPhone;
	String note;
}