package com.backend.dto.request.voucher;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.constant.Type.VoucherType;
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
public class VoucherCreationRequest {

	@Length(min = 10, max = 16)
	String code = Helpers.handleRandom(11);

	@NotNull
	@Length(min = 10, max = 50)
	String name;

	DiscountVoucherType discount_type = DiscountVoucherType.PERCENT;

	@NotNull
	Double value;

	@NotNull
	VoucherType voucher_category;

	Double max_discount;
	
	Double min_order;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime expiry_date;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime start_date;

	@Min(1)
	int usage_limit;

	Boolean isDestroy = false;

	Boolean isPublic = true;
	
	@NotNull
	Boolean applyAll;

	Set<Product> products;
}
