package com.backend.specification;

import com.backend.entity.test.Attribute;
import com.backend.entity.test.AttributeOption;
import com.backend.entity.test.AttributeOptionSku;
import com.backend.entity.test.Product;
import com.backend.entity.test.Sku;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    // Điều kiện lọc theo Category ID
    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    // Điều kiện lọc theo Attributes
    public static Specification<Product> hasAttributes(Map<String, String> attributes) {
        return (root, query, criteriaBuilder) -> {
            if (attributes == null || attributes.isEmpty()) {
                return criteriaBuilder.conjunction(); // Không có điều kiện lọc
            }

            Predicate predicate = criteriaBuilder.conjunction();
            Join<Product, Sku> skuJoin = root.join("skus", JoinType.LEFT);
            Join<Sku, AttributeOptionSku> aosJoin = skuJoin.join("attributeOptionSkus", JoinType.LEFT);
            Join<AttributeOptionSku, AttributeOption> aoJoin = aosJoin.join("attributeOption", JoinType.INNER);
            Join<AttributeOption, Attribute> attributeJoin = aoJoin.join("attribute", JoinType.INNER);

            // Xây dựng điều kiện lọc cho từng thuộc tính
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String attributeName = entry.getKey();
                String attributeValue = entry.getValue();

                // Thêm điều kiện lọc cho từng thuộc tính
                Predicate attributePredicate = criteriaBuilder.and(
                        criteriaBuilder.equal(attributeJoin.get("name"), attributeName),
                        criteriaBuilder.equal(aoJoin.get("value"), attributeValue)
                );

                // Kết hợp tất cả các điều kiện lọc
                predicate = criteriaBuilder.and(predicate, attributePredicate);
            }

            return predicate;
        };
    }

    // Lấy thông tin của SKU và Attribute
    public static Specification<Product> getProductWithSkus() {
        return (root, query, criteriaBuilder) -> {
            // Fetch SKUs và các thuộc tính liên quan
            root.fetch("skus", JoinType.LEFT).fetch("attributeOptionSkus", JoinType.LEFT)
                .fetch("attributeOption", JoinType.LEFT).fetch("attribute", JoinType.LEFT);

            // Thêm logic để tránh dữ liệu bị duplicate khi fetch nhiều join
            query.distinct(true);

            return criteriaBuilder.conjunction();
        };
    }

}
