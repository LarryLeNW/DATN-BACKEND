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

	@Column(name = "code")
	private String code;

	@Column(name = "price")
	private Long price;

	@Column(name = "stock")
	private Long stock;

	@Column(name = "discount")
	private Long discount;

	@OneToMany(mappedBy = "sku")
	private List<AttributeOptionSku> attributeOptionSkus;

	@Column(name = "images", columnDefinition = "NVARCHAR(MAX)")
	private String images;

	public Sku(Product product, String code, Long price, Long stock, Long discount, String images) {
		super();
		this.product = product;
		this.code = code;
		this.price = price;
		this.stock = stock;
		this.discount = discount;
		this.images = images;
	}

	@Override
	public String toString() {
		return "Sku{id=" + id + ", code='" + code + "'}";
	}

}