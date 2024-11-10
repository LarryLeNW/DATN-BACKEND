//package com.backend.entity;
//
//import java.sql.Timestamp;
//
//import com.backend.constant.Type.OrderStatusType;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotNull;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.FieldDefaults;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Entity
//@Table(name = "delivery")
//public class Delivery {
//	@Id
//	@GeneratedValue(strategy = GenerationType.UUID)
//	String id;
//
//	@NotNull 
//	@Column(name = "username", columnDefinition = "NVARCHAR(MAX)")
//	String username; 
//	
//	@NotNull
//	@Column(name = "address", columnDefinition = "NVARCHAR(MAX)")
//	String address; 
//	
//	@Column(name = "email")
//	String email; 
//	
//	@NotNull
//	@Column(name = "numberPhone")
//	String numberPhone; 
//	
//	@Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
//	String note;
//	
//}
