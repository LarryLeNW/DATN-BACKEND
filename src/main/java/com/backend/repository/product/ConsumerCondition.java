package com.backend.repository.product;

import java.util.function.Consumer;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConsumerCondition implements Consumer<SearchType> {
    private CriteriaBuilder builder;
    private Predicate predicate;
    private Root<?> root;
    private Join<?, ?> attributeJoin; // Join trực tiếp với AttributeProduct

    @Override
    public void accept(SearchType criteria) {
        String key = criteria.getKey();
        String operation = criteria.getOperation();
        Object value = criteria.getValue();

        if (isProductField(key)) {
            // Xử lý các trường của Product
            handleSearch(root, key, operation, value);
        } else {
            // Nếu không có attributeJoin, thực hiện join động với bảng AttributeProduct
            if (attributeJoin == null) {
                attributeJoin = root.join("attributes", JoinType.LEFT);  // Tạo join với bảng AttributeProduct
            }
            // Xử lý lọc trên bảng AttributeProduct
            handleSearch(attributeJoin, key, operation, value);
        }
    }

    private boolean isProductField(String key) {
        return isValidField(root, key); // Kiểm tra xem key có thuộc về bảng Product không
    }

    private boolean isValidField(From<?, ?> join, String key) {
        try {
            join.get(key); // Kiểm tra xem key có tồn tại trong join không
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void handleSearch(From<?, ?> from, String field, String operation, Object value) {
        if (value == null) {
            return; // Bỏ qua giá trị null
        }

        if (predicate == null) {
            predicate = builder.conjunction(); // Khởi tạo nếu predicate đang là null
        }

        switch (operation) {
            case "=":
                predicate = builder.and(predicate, builder.equal(from.get(field), value));
                break;
            case ">":
                predicate = builder.and(predicate, builder.greaterThan(from.get(field), (Comparable) value));
                break;
            case "<":
                predicate = builder.and(predicate, builder.lessThan(from.get(field), (Comparable) value));
                break;
            case ":": // Xử lý phép tìm kiếm "like"
                predicate = builder.and(predicate, builder.like(from.get(field), "%" + value + "%"));
                break;
            default:
                throw new UnsupportedOperationException("Operation not supported: " + operation);
        }
    }

    public Predicate getPredicate() {
        return this.predicate;
    }
}
