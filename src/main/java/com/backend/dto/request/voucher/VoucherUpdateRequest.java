package com.backend.dto.request.voucher;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.constant.Type.DiscountVoucherType;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.entity.Product;
import com.backend.entity.Role;
import com.backend.utils.Helpers;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class VoucherUpdateRequest {

	String code ;
	
	String name ;

	DiscountVoucherType discount_type ;

	Double value; 
	
	Double max_discount;

	Double min_order;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime expiry_date;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime start_date;
	
	@Min(1)
	int usage_limit;
	
	Boolean isDestroy ; 
	
	Boolean isPublic ; 
	
	Set<Product> products;
}
