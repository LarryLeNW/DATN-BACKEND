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
@Table(name = "orderItems")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	@ManyToMany
	Set<Product> product;
	
	double quantity;
	
	double price;
	
}
