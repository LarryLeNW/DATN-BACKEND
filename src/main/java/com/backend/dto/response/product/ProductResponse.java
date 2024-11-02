package com.backend.dto.response.product;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Sku;

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
	Category category;
	Brand brand;
	String description;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
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
		String images;
		HashMap<String, String> attributes;

	}

}
