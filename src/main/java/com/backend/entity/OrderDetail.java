package com.backend.entity;

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
@Table(name = "order_details")
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name = "quantity",nullable = false, columnDefinition = "FLOAT DEFAULT 1.0")
	double quantity;

	@Column(name = "price", nullable = false, columnDefinition = "FLOAT DEFAULT 0.0")
	double price;

	@ManyToOne
	@JoinColumn(name = "orderId", nullable = false)
	@JsonIgnore
	private Order order;

	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	private Product product;

	@ManyToOne
	@JoinColumn(name = "skuid", nullable = false)
	private Sku sku;
	
	@Column(name = "is_review", nullable = true, columnDefinition = "BIT DEFAULT 0")
	Boolean isReview;



}
