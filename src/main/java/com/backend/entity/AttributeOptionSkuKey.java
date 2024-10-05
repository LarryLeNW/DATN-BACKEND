package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class AttributeOptionSkuKey implements Serializable {

    private Long skuId;
    private Long attributeOptionId;

    // Default constructor
    public AttributeOptionSkuKey() {}

    // Getters and Setters, equals, hashCode
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