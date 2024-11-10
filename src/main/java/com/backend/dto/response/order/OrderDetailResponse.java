package com.backend.dto.response.order;




import com.backend.dto.response.product.ProductResponse.SKUDTO;

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

	String id;
    long productId;
    String productName;
	String orderId;
	SKUDTO sku;
	
    
}