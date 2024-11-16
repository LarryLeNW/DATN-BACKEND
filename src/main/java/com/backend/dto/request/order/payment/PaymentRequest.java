package com.backend.dto.request.order.payment;

import com.backend.constant.Type.PaymentMethod;
import com.backend.constant.Type.PaymentStatus;
import com.backend.dto.request.order.delivery.DeliveryRequest;

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
public class PaymentRequest {
	double amount; 
	PaymentMethod method; 
	PaymentStatus status;
}
