package com.backend.dto.request.voucher;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.constant.Type.VoucheType;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.entity.Product;
import com.backend.entity.Role;

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
public class VoucherCreationRequest {

	@NotNull
	@Length(min = 10, max = 16)
	String code;
	
	@NotNull
	@Length(min = 10, max = 16)
	String name ;

	VoucheType discount_type = VoucheType.PERCENT;

	@NotNull
	Double value; 
	
	
	Double max_discount;

	Double min_order;

	@NotNull
	Double expiry_date;
	
	@Min(2)
	int usage_limit;
	
	Boolean isDestroy = false; 
	
	Boolean isPublic = true; 
	
	Set<Product> products;
}
