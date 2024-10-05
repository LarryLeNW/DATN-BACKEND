package com.backend.entity;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "skus")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
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

	@OneToMany
    private List<Image> images;	
	
	
	public Sku(Product product, String code, Long price, Long stock, Long discount) {
		super();
		this.product = product;
		this.code = code;
		this.price = price;
		this.stock = stock;
		this.discount = discount;
	}
	
	@Override
	public String toString() {
	    return "Sku{id=" + id + ", code='" + code + "'}"; 
	}

    
    
}
