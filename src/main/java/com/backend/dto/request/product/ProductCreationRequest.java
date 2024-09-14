package com.backend.dto.request.product;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.backend.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {
	@Valid
	@NotNull(message = "Name cannot be empty")
	String name;

	String description;

	@Positive(message = "Price must be greater than 0")
	double price;

	@Min(value = 1, message = "Stock must be at least 1")
	int stock;

	@NotNull(message = "Thumbnail URL cannot be empty")
	@Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
	String thumbnail_url;
	
	@NotNull(message = "Thumbnail URL cannot be null")
	String categoryId;

	@NotNull(message = "Thumbnail URL cannot be null")
	String brandId;

}