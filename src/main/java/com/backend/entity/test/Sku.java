package com.backend.entity.test;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "skus")
@Data
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    private String code;
    
    private Long price;
    
    private Long stock; 
    
    private Long discount;
    

    @OneToMany(mappedBy = "sku")
    private List<AttributeOptionSku> attributeOptionSkus;

    // Getters and Setters
}
