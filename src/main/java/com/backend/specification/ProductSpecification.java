package com.backend.specification;

import com.backend.entity.Attribute;
import com.backend.entity.AttributeOption;
import com.backend.entity.AttributeOptionSku;
import com.backend.entity.Product;
import com.backend.entity.Sku;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

	  public static Specification<Product> hasSkuSortField(String sortField, Sort.Direction direction) {
	        return (root, query, criteriaBuilder) -> {
	            Join<Product, Sku> skuJoin = root.join("skus", JoinType.LEFT);

	            if ("discount".equals(sortField)) {
	                if (direction == Sort.Direction.ASC) {
	                    query.orderBy(criteriaBuilder.asc(skuJoin.get("discount")));
	                } else {
	                    query.orderBy(criteriaBuilder.desc(skuJoin.get("discount")));
	                }
	            } else if ("price".equals(sortField)) {
	                if (direction == Sort.Direction.ASC) {
	                    query.orderBy(criteriaBuilder.asc(skuJoin.get("price")));
	                } else {
	                    query.orderBy(criteriaBuilder.desc(skuJoin.get("price")));
	                }
	            } else if ("stock".equals(sortField)) {
	                if (direction == Sort.Direction.ASC) {
	                    query.orderBy(criteriaBuilder.asc(skuJoin.get("stock")));
	                } else {
	                    query.orderBy(criteriaBuilder.desc(skuJoin.get("stock")));
	                }
	            }

	            return criteriaBuilder.conjunction();
	        };
	    }


	public static Specification<Product> hasExactPrice(Long price) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("skus").get("price"), price);
	}

	public static Specification<Product> hasAttributes(Map<String, String> attributes) {
		return (root, query, criteriaBuilder) -> {
			if (attributes == null || attributes.isEmpty()) {
				return criteriaBuilder.conjunction(); 
			}

			Predicate predicate = criteriaBuilder.conjunction();
			Join<Product, Sku> skuJoin = root.join("skus", JoinType.LEFT);
			Join<Sku, AttributeOptionSku> aosJoin = skuJoin.join("attributeOptionSkus", JoinType.LEFT);
			Join<AttributeOptionSku, AttributeOption> aoJoin = aosJoin.join("attributeOption", JoinType.INNER);
			Join<AttributeOption, Attribute> attributeJoin = aoJoin.join("attribute", JoinType.INNER);

			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				String attributeName = entry.getKey();
				String attributeValue = entry.getValue();

		        Predicate attributePredicate = criteriaBuilder.and(
	                    criteriaBuilder.like(criteriaBuilder.lower(attributeJoin.get("name")), "%" + attributeName.toLowerCase() + "%"),
	                    criteriaBuilder.like(criteriaBuilder.lower(aoJoin.get("value")), "%" + attributeValue.toLowerCase() + "%"));

				predicate = criteriaBuilder.and(predicate, attributePredicate);
			}

			return predicate;
		};
	}

	public static Specification<Product> hasMinPrice(Long minPrice) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.join("skus").get("price"),
				minPrice);
	}

	public static Specification<Product> hasMaxPrice(Long maxPrice) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.join("skus").get("price"),
				maxPrice);
	}

	public static Specification<Product> getProductWithSkus() {
		return (root, query, criteriaBuilder) -> {
			root.fetch("skus", JoinType.LEFT).fetch("attributeOptionSkus", JoinType.LEFT)
					.fetch("attributeOption", JoinType.LEFT).fetch("attribute", JoinType.LEFT);

			query.distinct(true);

			return criteriaBuilder.conjunction();
		};
	}

}
