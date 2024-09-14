 package com.backend.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	String name;

	String description;
	
	double price;
	
	int stock; 
	
	String thumbnail_url; 

	@ManyToOne
	Category category;
	
	@ManyToOne
	Brand brand;
	
	@CreationTimestamp
    LocalDateTime createdAt;
 
    @UpdateTimestamp
    LocalDateTime updatedAt;
	
}
