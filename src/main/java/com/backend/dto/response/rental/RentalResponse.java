package com.backend.dto.response.rental;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.constant.Type.OrderStatusType;
import com.backend.constant.Type.RentalStatus;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.order.OrderUserResponse;
import com.backend.dto.response.order.PaymentResponse;
import com.backend.entity.Delivery;
import com.backend.entity.rental.RentalPackage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentalResponse {
	String id;
	double totalAmount;
	String rentalCode; 
	Delivery delivery;
	OrderUserResponse user;
	PaymentResponse payment;
	List<RentailDetailReponse> rentalDetails;
	RentalPackage rentalPackage;
	RentalStatus status;
	Double discountValue;
}
