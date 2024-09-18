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
@Table(name = "reviews")
public class Review {
	
	public enum OrderStatus {
	    PENDING,
	    SHIPPED,
	    DELIVERED,
	    CANCELLED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	@ManyToOne
	User reviewBy;
	
	@ManyToOne
	Product product; 
	
	int rating;
	
	String review_text; 
	
	Timestamp review_date;
	
	@Enumerated(EnumType.STRING)
	OrderStatus status;
}
