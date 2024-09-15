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
@Table(name = "carts")
public class Cart {
	

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	
}
