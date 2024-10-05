// package com.backend.entity;
//
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.util.Set;
//
//import com.backend.constant.Type.OrderStatusType;
//
//import jakarta.persistence.*;
//
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
//@Table(name = "orders")
//public class Order {
//	
//	
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.UUID)
//	String id;
//
//	Timestamp order_date;
//
//	double total_amount;
//	
//	@Enumerated(EnumType.STRING)
//	OrderStatusType status;
//
//}
