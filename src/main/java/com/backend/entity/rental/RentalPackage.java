package com.backend.entity.rental;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.entity.Product;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rental_packages")
public class RentalPackage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "name", nullable = false)
	String name;

	@Column(name = "duration_days", nullable = false)
    Long durationDays;  

    @Column(name = "price", nullable = false)
    Long price;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "discount_percentage", nullable = true)
    Long discountPercentage;  

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
