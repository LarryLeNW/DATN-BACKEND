 package com.backend.entity;

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
@Table(name = "brands")
public class Brand {
	
	enum OrderStatus {
	    PENDING,
	    SHIPPED,
	    DELIVERED,
	    CANCELLED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	String name;
	
	@ManyToOne
	User orderBy; 

	@Enumerated(EnumType.STRING)
	OrderStatus status;
}
