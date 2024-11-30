package com.backend.specification;

import com.backend.entity.Attribute;
import com.backend.entity.AttributeOption;
import com.backend.entity.AttributeOptionSku;
import com.backend.entity.Cart;
import com.backend.entity.Product;
import com.backend.entity.Sku;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

public class CartSpecification {
	
    public static Specification<Cart> belongsToUser(String userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Cart> hasQuantity(Double quantity) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("quantity"), quantity);
    }

}
