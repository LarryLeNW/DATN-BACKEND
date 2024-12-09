package com.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.entity.rental.RentalPackage;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "products")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", columnDefinition = "NVARCHAR(MAX)")
    String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    String description;

    String slug;

    @Column(name = "total_sold")
    Long totalSold;

    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    @JsonIgnore
    Category category;

    @ManyToOne
    @JoinColumn(name = "brandId", nullable = true)
    @JsonIgnore
    Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Sku> skus;

    @Column(name = "stars")
    Double stars;

    @ManyToMany(mappedBy = "products")
    List<Voucher> vouchers;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "'}";
    }
    
    @Column(name = "can_be_rented")
    Boolean canBeRented = false;  

    @Column(name = "hourly_rent_price")
    Long hourlyRentPrice;  

    @Column(name = "daily_rent_price")
    Long dailyRentPrice;  

    @Column(name = "min_rental_quantity")
    Long minRentalQuantity;  

    @Column(name = "max_rental_quantity")
    Long maxRentalQuantity;  
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<RentalPackage> rentalPackages; 
    
    @PrePersist
    public void prePersist() {
        if (stars == null) {
            stars = 5.0;
        }
        if (totalSold == null) {
            totalSold = 0L;  
        }
    }
}
