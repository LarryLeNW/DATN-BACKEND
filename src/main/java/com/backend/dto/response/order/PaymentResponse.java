package com.backend.dto.response.order;

import java.time.LocalDateTime;


import com.backend.constant.Type.PaymentMethod;
import com.backend.constant.Type.PaymentStatus;

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
public class PaymentResponse {
	
	Integer id;
	double amount; 
	PaymentMethod method; 
	PaymentStatus status;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
}
