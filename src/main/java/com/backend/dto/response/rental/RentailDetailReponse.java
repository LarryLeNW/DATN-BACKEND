package com.backend.dto.response.rental;

import java.time.LocalDateTime;

import com.backend.constant.Type.RentalStatus;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentailDetailReponse {
	Integer id;
	Double quantity;
    long productId;
    String productName;
	String rentalId;
	SKUDTO sku;
	double price;
	Long hour;
	Long day; 
	LocalDateTime startAt;
	LocalDateTime endAt;
	RentalStatus status;

}
