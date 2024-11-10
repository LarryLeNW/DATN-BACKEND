package com.backend.dto.request.order;

import java.util.List;

import com.backend.constant.Type.OrderStatusType;

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
public class OrderDetailCreationRequest {

    String id;
    double quantity;
    double price;
	String orderId;
    Long productId;    
    Long skuid;

}
