package com.backend.entity;

import java.util.Set;

import com.backend.constant.Type.VoucheType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "vouchers")
public class Voucher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "code")
	String code;

	@Column(name = "name", nullable = false)
	String name;
	
	@Column(name = "discount_type", nullable = false)
	VoucheType discount_type;

	@Column(name = "value")
	Double value;

	@Column(name = "max_discount")
	Double max_discount;

	@Column(name = "min_order")
	Double min_order;

	@Column(name = "expiry_date")
	Double expiry_date;

	@Column(name = "points", nullable = false, columnDefinition = "INT DEFAULT 1")
	int usage_limit;
	
	@Column(name = "usage_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	int usageCount;
	
	Boolean isPublic ; 
	
	Boolean isDestroy; 
	
	@ManyToMany
	@JoinTable(name = "voucher_product", 
			joinColumns = @JoinColumn(name = "voucher_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id") 
	)
	Set<Product> products;

}
