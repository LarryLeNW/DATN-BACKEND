package com.backend.dto.request.product;

import java.util.HashMap;
import java.util.List;

import com.backend.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {
	Long id;
	String name;
	String slug;
	Long categoryId;
	Long brandId;
	String description;
	List<SKUDTO> skus;
	
	List<RentalPackageDTO> rentalPackages;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RentalPackageDTO {
		String name;
	    Long durationDays;  
	    Long price;
	    Long discountPercentage;  
	    Boolean isActive = true;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SKUDTO {
		private Long id;
		private Long price;
		private Long stock;
		private Long discount;
		private String code;
		private Boolean canBeRented;
		private Long hourlyRentPrice;
		private Long dailyRentPrice;
		private Long minRentalQuantity;
		private Long maxRentalQuantity;
		private HashMap<String, String> attributes;
		private String images;
	}
}
