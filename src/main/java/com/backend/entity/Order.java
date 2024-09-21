 package com.backend.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;


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
@Table(name = "orders")
public class Order {
	
	public enum OrderStatus {
	    PENDING,
	    SHIPPED,
	    DELIVERED,
	    CANCELLED
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	Timestamp order_date;

	double total_amount;
	
	@Enumerated(EnumType.STRING)
	OrderStatus status;

}
