package com.backend.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.PaymentMethod;
import com.backend.constant.Type.PaymentStatus;

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

	@OneToOne
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;

	@ManyToOne
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
