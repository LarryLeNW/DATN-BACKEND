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
@Table(name = "payments")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;
	
	@ManyToOne
	Order order;

	Payment_Method method;
	
	Status_Payment status;
	
	Timestamp payment_date;
	
	enum Payment_Method {
		CreditCard,
		PayPal,
		COD
	}
	
	enum Status_Payment {
		CreditCard,
		PayPal,
		COD
	}
	
	

}
