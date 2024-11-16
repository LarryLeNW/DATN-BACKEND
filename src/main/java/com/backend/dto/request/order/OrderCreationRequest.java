package com.backend.dto.request.order;


import java.util.List;

import com.backend.constant.Type.OrderStatusType;
import com.backend.dto.request.order.delivery.DeliveryRequest;
import com.backend.dto.request.order.payment.PaymentRequest;
import com.backend.entity.Delivery;

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
public class OrderCreationRequest {

	double totalAmount;
	OrderStatusType status;
	String userId;
	DeliveryRequest delivery;
    List<OrderDetailCreationRequest> orderDetails; 
    PaymentRequest payment;
    

}
