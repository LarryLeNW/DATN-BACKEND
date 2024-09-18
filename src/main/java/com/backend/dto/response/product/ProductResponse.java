package com.backend.dto.response.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.backend.dto.response.auth.RoleResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String name;
    String description;
    double price;
	int stock;
	String thumbnail_url;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
}
