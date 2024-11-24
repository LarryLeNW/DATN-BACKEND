package com.backend.entity;

import java.sql.Timestamp;

import com.backend.constant.Type.AddressType;
import com.backend.constant.Type.OrderStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "delivery")
public class Delivery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@NotNull
	@Column(name = "username", columnDefinition = "NVARCHAR(MAX)")
	String username;

	String company_name;

	@NotNull
	@Column(name = "numberPhone")
	String numberPhone;
	
	String city_id; 
	
	@Column(name = "city", columnDefinition = "NVARCHAR(MAX)")
	String city; 
	
	String district_id;
	
	@Column(name = "district", columnDefinition = "NVARCHAR(MAX)")
	String district;
	
	String ward_id; 

	@Column(name = "ward", columnDefinition = "NVARCHAR(MAX)")
	String ward;
	
	@Column(name = "street", columnDefinition = "NVARCHAR(MAX)")
	String street;
	
	AddressType typeAddress;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	User user;

	@NotNull
	Boolean isDefault;
	
}
