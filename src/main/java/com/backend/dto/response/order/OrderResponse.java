package com.backend.dto.response.order;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.constant.Type.OrderStatusType;
import com.backend.entity.Delivery;
import com.backend.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

	String id;
	double total_amount;
	OrderStatusType status;
	Delivery delivery;
	OrderUserResponse user;
	PaymentResponse payment;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
	List<OrderDetailResponse> orderDetails;

}
