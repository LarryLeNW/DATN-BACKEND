package com.backend.entity.rental;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.constant.Type.RentalStatus;
import com.backend.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
	@Column(name = "rental_code", nullable = false, unique = true)
	String rentalCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user;

    @Column(name = "total_amount", nullable = false)
    double totalAmount;
    
    @OneToMany(mappedBy = "rental", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<RentalDetail> rentalDetails;
    
    @ManyToOne
    @JoinColumn(name = "rental_package_id")
    @JsonIgnore
    RentalPackage rentalPackage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    RentalStatus status;
    
    @ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "delivery_id", referencedColumnName = "id")
	Delivery delivery;

    @Column(name = "discount_value")
	Double discountValue;
    
    @OneToOne(mappedBy = "rental", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	@JsonIgnore
	Payment payment;
    
    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
