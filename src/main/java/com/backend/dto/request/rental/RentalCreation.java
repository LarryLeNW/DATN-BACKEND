package com.backend.dto.request.rental;

import java.util.List;

import com.backend.dto.request.delivery.DeliveryCreationRequest;
import com.backend.dto.request.order.OrderDetailCreationRequest;
import com.backend.dto.request.order.payment.PaymentRequest;
import com.backend.entity.Voucher;
import com.backend.entity.rental.RentalPackage;
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
public class RentalCreation {
	DeliveryCreationRequest delivery;
	List<DetailRentalCreation> detailRentals;
	PaymentRequest payment;
	List<Voucher> vouchers;
	String code = Helpers.handleRandom(11);
	Double discountValue;
	RentalPackage rentalPackage;
}
