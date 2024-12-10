package com.backend.dto.response.product;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Sku;
import com.backend.entity.Voucher;
import com.backend.entity.rental.RentalPackage;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
	Long id;
	String name;
	String slug;
	CategoryDTO category;
	BrandDTO brand;
	String description;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
	Double stars;
	Long totalSold;
	List<RentalPackage> rentalPackages;

	private List<SKUDTO> skus;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SKUDTO {
		Long Id;
		Long price;
		Long stock;
		Long discount;
		String code;
		Boolean canBeRented ;
		Long hourlyRentPrice;
		Long dailyRentPrice;
		Long minRentalQuantity;
		Long maxRentalQuantity;

		String images;
		HashMap<String, String> attributes;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class BrandDTO {
		Long id;
		String name;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CategoryDTO {
		Long id;
		String name;
	}

}
