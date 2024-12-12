package com.backend.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.PaymentMethod;
import com.backend.constant.Type.PaymentStatus;
import com.backend.entity.rental.Rental;
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
@Table(name = "payments")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name = "app_trans_id", nullable = false, unique = true)
	String appTransId;

	@OneToOne
	@JoinColumn(name = "orderId")
	private Order order;
	
	@OneToOne
	@JoinColumn(name = "rentalId")
	private Rental rental;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "userid", nullable = false)
	
	private User user;

	@Column(nullable = false)
	private Double amount;

	@Enumerated(EnumType.STRING)
	PaymentMethod method;

	@Enumerated(EnumType.STRING)
	PaymentStatus status;

	@CreationTimestamp
	@Column(name = "created_at")
	LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

}
