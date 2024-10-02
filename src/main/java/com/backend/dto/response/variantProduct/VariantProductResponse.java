package com.backend.dto.response.variantProduct;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.entity.AttributeProduct;

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
public class VariantProductResponse {
	 String id; 
	    String name; 
	    String description; 
	    double price; 
	    int stock; 
	    String thumbnail_url; 
	    List<AttributeProduct> attributes; 
	    LocalDateTime createdAt; 
	    LocalDateTime updatedAt; 
}
