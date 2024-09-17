package com.backend.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
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
@Table(name = "brands")
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	@Column(name = "name", columnDefinition = "NVARCHAR(255)", unique = true)
	String name;

    @CreationTimestamp
    LocalDateTime createdAt;
 
    @UpdateTimestamp
    LocalDateTime updatedAt;
 
}
