package com.backend.dto.request.variantProduct;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.entity.AttributeProduct;
import com.backend.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class VariantProductCreationRequest {

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

	@NotNull(message = "Product cannot be null")
	String productId;
	
	@NotNull
	List<AttributeProduct> attributes; 
}
