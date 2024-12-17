package com.backend.dto.request.rental;

import java.util.List;

import com.backend.constant.Type.RentalStatus;
import com.backend.dto.request.delivery.DeliveryCreationRequest;
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
public class RentalUpdateRequest {
	Long id;
	DeliveryCreationRequest delivery;
	List<DetailRentalCreation> detailRentals;
	Long totalAmount ;
	List<Voucher> vouchers;
	Double discountValue;
	RentalPackage rentalPackage;
	RentalStatus status;
}
