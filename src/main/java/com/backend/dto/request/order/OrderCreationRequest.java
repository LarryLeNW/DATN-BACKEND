package com.backend.dto.request.order;


import java.util.List;

import com.backend.constant.Type.OrderStatusType;
import com.backend.dto.request.delivery.DeliveryCreationRequest;
import com.backend.dto.request.order.delivery.DeliveryRequest;
import com.backend.dto.request.order.payment.PaymentRequest;
import com.backend.entity.Delivery;
import com.backend.entity.Voucher;
import com.backend.utils.Helpers;

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
	DeliveryCreationRequest delivery;
    List<OrderDetailCreationRequest> orderDetails; 
    PaymentRequest payment;
    List<Voucher> vouchers;
    String code = Helpers.handleRandom(11);
    Double discountValue;
}
