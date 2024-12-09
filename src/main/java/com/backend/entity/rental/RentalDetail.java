package com.backend.entity.rental;

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

	@Column(name = "quantity",nullable = false, columnDefinition = "FLOAT DEFAULT 1.0")
	double quantity;

	@Column(name = "price", nullable = false, columnDefinition = "FLOAT DEFAULT 0.0")
	double price;

	@ManyToOne
	@JoinColumn(name = "rentalId", nullable = false)
	@JsonIgnore
	private Rental rental;

	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	private Product product;

	@ManyToOne
	@JoinColumn(name = "skuid", nullable = false)
	private Sku sku;

}
