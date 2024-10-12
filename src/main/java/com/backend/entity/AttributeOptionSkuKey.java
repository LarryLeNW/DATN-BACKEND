package com.backend.entity;


import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class AttributeOptionSkuKey implements Serializable {

    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "attribute_option_id")
    private Long attributeOptionId;

    // Default constructor
    public AttributeOptionSkuKey() {}

    // Constructor
    public AttributeOptionSkuKey(Long skuId, Long attributeOptionId) {
        this.skuId = skuId;
        this.attributeOptionId = attributeOptionId;
    }

    // Equals and HashCode methods for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeOptionSkuKey that = (AttributeOptionSkuKey) o;
        return Objects.equals(skuId, that.skuId) &&
               Objects.equals(attributeOptionId, that.attributeOptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuId, attributeOptionId);
    }
}
