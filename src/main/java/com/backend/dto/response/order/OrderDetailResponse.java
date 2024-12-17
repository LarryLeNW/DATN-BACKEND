package com.backend.dto.response.order;

import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;
import com.backend.entity.Product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
	Integer id;
	Double quantity;
    long productId;
    String productName;
	String orderId;
	SKUDTO sku;
	double price;
	ProductResponse product;
	Boolean isReview;
}