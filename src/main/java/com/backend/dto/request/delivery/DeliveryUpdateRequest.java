package com.backend.dto.request.delivery;

import java.time.LocalDateTime;
import java.util.Set;

import com.backend.constant.Type.AddressType;
import com.backend.constant.Type.DiscountVoucherType;
import com.backend.constant.Type.VoucherType;
import com.backend.dto.request.voucher.VoucherCreationRequest;
import com.backend.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class DeliveryUpdateRequest {

	String username;
	
	String company_name;

	String numberPhone;

	String city_id;

	String city;
	
	String district_id;

	String district;
	
	String ward_id;

	String ward;
	
	String street;

	AddressType typeAddress;

	Boolean isDefault;
}
