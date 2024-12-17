package com.backend.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.backend.constant.Type.DiscountVoucherType;
import com.backend.constant.Type.VoucherType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Column(name = "code",nullable = false,unique = true)
	String code;

	@Column(name = "name", nullable = false,unique = true, columnDefinition = "NVARCHAR(MAX)")
	String name;
	
	@Column(name = "discount_type", nullable = false)
	DiscountVoucherType discount_type;

    @Column(name = "voucher_category", nullable = false)
    @Enumerated(EnumType.STRING)
    VoucherType voucher_category;
    
	@Column(name = "value")
	Double value;

	@Column(name = "max_discount")
	Double max_discount;

	@Column(name = "min_order")
	Double min_order;

	@Column(name = "start_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime start_date;
	
	@Column(name = "expiry_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime expiry_date;

	@Column(name = "points", nullable = false, columnDefinition = "INT DEFAULT 1")
	int usage_limit;
	
	@Column(name = "usage_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	int usageCount;
	
	Boolean isPublic ; 
	
	Boolean isDestroy; 
	
	Boolean applyAll;
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "voucher_product", 
			joinColumns = @JoinColumn(name = "voucher_id"),
			inverseJoinColumns = @JoinColumn(name = "product_id") 
	)
	Set<Product> products;
	
	@ManyToMany(mappedBy = "vouchers") 
	List<User> users; 


	
}
