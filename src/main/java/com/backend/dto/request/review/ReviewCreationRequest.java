package com.backend.dto.request.review;


import java.sql.Timestamp;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCreationRequest {

   public enum OrderStatus {
        PENDING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    String id;

    @NotNull(message = "riviewbyId cannot be null.")
    String review_by_id;

    @NotNull(message = "productId cannot be null.")
    String product_id;

    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must be at most 5.")
    int rating;

    @NotBlank(message = "Review text cannot be blank.")
    String review_text;

    Timestamp review_date;	

    @NotNull(message = "Order status is required.")
    @Enumerated(EnumType.STRING)
    OrderStatus status;
}
