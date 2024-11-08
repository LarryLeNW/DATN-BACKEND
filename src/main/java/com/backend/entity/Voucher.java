package com.backend.entity;

import java.util.Set;

import com.backend.constant.Type.VoucheType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "voucher")
public class Voucher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String code;

	@NotNull
	VoucheType discount_type;

	Double value;

	Double max_discount;

	Double min_order;

	Double expiry_date;

	@Column(name = "points", nullable = false, columnDefinition = "INT DEFAULT 1")
	int usage_limit;
	
	Boolean isPublic ; 
	
	Boolean isDestroy; 
}
