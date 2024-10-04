//package com.backend.dto.request.product;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import com.backend.entity.AttributeProduct;
//import com.backend.validator.DobConstraint;
//
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Positive;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class ProductUpdateRequest {
//	String name;
//
//	String description;
//
//	@Positive(message = "Price must be greater than 0")
//	Double price; 
//
//	@Min(value = 1, message = "Stock must be at least 1")
//	Integer stock;
//	
//	@Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
//	String thumbnail_url;
//	
//	String categoryId;
//
//	String brandId;
//
//	List<AttributeProduct> attributes ; 
//}
