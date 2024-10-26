// package com.backend.entity;
//
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Set;
//
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Entity
//@Table(name = "reviews")
//public class Review {
//	@Id
//	@GeneratedValue(strategy = GenerationType.UUID)
//	String id;
//
//	@ManyToOne
//	User reviewBy;
//	
//	@ManyToOne
//	Product product; 
//	
//	@NotNull
//	int rating;
//	
//	String review_text; 
//
//	String image_url; 
//	
//	String video_url;
//	
//	@CreationTimestamp
//	LocalDateTime createdAt;
//
//	@UpdateTimestamp
//	LocalDateTime updatedAt;
//}
