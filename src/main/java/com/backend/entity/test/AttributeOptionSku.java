package com.backend.entity.test;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attribute_option_sku")
@Data
public class AttributeOptionSku {

    @EmbeddedId
    private AttributeOptionSkuKey id;

    @ManyToOne
    @MapsId("skuId")
    @JoinColumn(name = "sku_id")
    @JsonIgnore
    private Sku sku;

    @ManyToOne
    @MapsId("attributeOptionId")
    @JoinColumn(name = "attribute_option_id")
    @JsonIgnore
    private AttributeOption attributeOption;

    // Getters and Setters
}