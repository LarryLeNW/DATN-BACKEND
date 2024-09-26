package com.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "address")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;

	@Column(name = "street")
	String street;

	@Column(name = "city")
	String city;

	@Column(name = "state")
	String state;

	@Column(name = "postal_code")
	String postalCode;

	@Column(name = "country")
	String country;
}
