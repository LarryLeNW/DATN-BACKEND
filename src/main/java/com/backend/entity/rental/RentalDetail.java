package com.backend.entity.rental;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;

import com.backend.constant.Type.RentalStatus;
import com.backend.entity.Product;
import com.backend.entity.Sku;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "rentalDetails")
public class RentalDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name = "quantity", nullable = false, columnDefinition = "FLOAT DEFAULT 1.0")
	double quantity;

	@Column(name = "price", nullable = false, columnDefinition = "FLOAT DEFAULT 0.0")
	double price;

	@ManyToOne
	@JoinColumn(name = "rentalId", nullable = false)
	@JsonIgnore
	Rental rental;

	Long hour;

	Long day;

	@CreationTimestamp
	@Column(name = "start_at")
	LocalDateTime startAt;

	@Column(name = "end_at")
	LocalDateTime endAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	RentalStatus status;

	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	Product product;

	@ManyToOne
	@JoinColumn(name = "skuid", nullable = false)
	Sku sku;

	@Column(name = "is_review", nullable = true, columnDefinition = "BIT DEFAULT 0")
	Boolean isReview;


}
