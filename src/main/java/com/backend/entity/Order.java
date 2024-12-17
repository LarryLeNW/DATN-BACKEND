package com.backend.entity;

import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.OrderStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name = "order_code", nullable = false, unique = true)
	String orderCode;

	double total_amount;

	@Enumerated(EnumType.STRING)
	OrderStatusType status;

	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL,orphanRemoval = false)
	List<OrderDetail> orderDetails;

	@ManyToOne
	@JoinColumn(name = "userid", nullable = true)
	@JsonIgnore
	User user;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "delivery_id", referencedColumnName = "id")
	Delivery delivery;

	@Column(name = "discount_value")
	Double discountValue;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	@JsonIgnore
	Payment payment;

	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

}
