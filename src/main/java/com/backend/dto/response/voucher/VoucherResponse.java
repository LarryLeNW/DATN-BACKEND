package com.backend.dto.response.voucher;

import java.util.List;
import java.util.Set;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.constant.Type.VoucheType;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.Address;
import com.backend.entity.Product;
import com.backend.entity.Voucher;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class VoucherResponse {
	Long id;

	String code;

	VoucheType discount_type;

	Double value;

	String name;
	
	Double max_discount;

	Double min_order;

	Double expiry_date;

	int usage_limit;
	
	int usageCount;
	
	Boolean isPublic ; 
	
	Boolean isDestroy; 
	
	List<ProductResponse> products;
	
}
