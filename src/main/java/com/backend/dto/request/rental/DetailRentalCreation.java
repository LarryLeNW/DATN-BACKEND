package com.backend.dto.request.rental;

import com.backend.constant.Type.RentalStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailRentalCreation {

	double quantity;
	double price;
	Long hour;
	Long day; 
    Long productId;    
    Long skuId;
    RentalStatus status = RentalStatus.PENDING;
}
