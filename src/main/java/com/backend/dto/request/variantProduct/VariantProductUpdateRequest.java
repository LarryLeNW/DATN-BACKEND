package com.backend.dto.request.variantProduct;

import java.util.List;

import com.backend.entity.AttributeProduct;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
public class VariantProductUpdateRequest {

	@NotNull(message = "Name cannot be null")
	String name;

	String description;

	@Positive(message = "Price must be greater than 0")
	double price;

	@Min(value = 1, message = "Stock must be at least 1")
	int stock;

	@NotNull(message = "Thumbnail URL cannot be empty")
	@Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
	String thumbnail_url;

	String productId;

	List<AttributeProduct> attributes; 
}
