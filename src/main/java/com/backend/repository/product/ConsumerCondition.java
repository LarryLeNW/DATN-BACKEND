package com.backend.repository.product;

import java.util.function.Consumer;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
	private Join<?, ?> variantJoin; // Join với VariantProduct
	private Join<?, ?> attributeJoin; // Join với AttributeProduct

	@Override
	public void accept(SearchType criteria) {
	    System.out.println("Processing search criteria: " + criteria);
	    String key = criteria.getKey();
	    String operation = criteria.getOperation();
	    Object value = criteria.getValue();
	    
	    System.out.println(isProductField(key));
	    
	    
	    if (isProductField(key)) {
	        handleSearch(root, key, operation, value);
	    } else {
	        // Nếu không phải, coi nó là thuộc tính động từ AttributeProduct
	        handleDynamicAttributeSearch(attributeJoin, key, operation, value);
	    }
	}

	private boolean isProductField(String key) {
		// Kiểm tra nếu key là thuộc tính của Product, thay thế bằng các trường hợp của
		// bảng Product
		try {
			root.get(key); // Nếu key tồn tại trong root của Product, nó là trường hợp hợp lệ
			return true;
		} catch (IllegalArgumentException e) {
			return false; // Nếu không tồn tại, coi key là thuộc tính động của AttributeProduct
		}
	}

	private void handleDynamicAttributeSearch(Join<?, ?> join, String attributeName, String operation, Object value) {
	    System.out.println("Searching for attribute: " + attributeName + " with value: " + value);
	    
	    // Lọc theo thuộc tính động từ AttributeProduct (ví dụ: color, size)

	    switch (operation) {
	        case ":": // Tìm kiếm như
	            predicate = builder.and(predicate, builder.like(join.get("value"), "%" + value + "%")); // value là giá trị
	            break;
	        case "=": // Tìm kiếm chính xác
	            predicate = builder.and(predicate, builder.equal(join.get("value"), value));
	            break;
	        case ">": // Tìm kiếm lớn hơn
	            predicate = builder.and(predicate, builder.greaterThan(join.get("value"), (Comparable) value));
	            break;
	        case "<": // Tìm kiếm nhỏ hơn
	            predicate = builder.and(predicate, builder.lessThan(join.get("value"), (Comparable) value));
	            break;
	        default:
	        	break ;
	    }
	    
	    System.out.println(operation);
	}


	private void handleSearch(From<?, ?> root, String field, String operation, Object value) {
		// Xử lý tìm kiếm cho các trường trong Product
		switch (operation) {
		case ":":
			predicate = builder.and(predicate, builder.like(root.get(field), "%" + value + "%"));
			break;
		case "=":
			predicate = builder.and(predicate, builder.equal(root.get(field), value));
			break;
		case ">":
			predicate = builder.and(predicate, builder.greaterThan(root.get(field), (Comparable) value));
			break;
		case "<":
			predicate = builder.and(predicate, builder.lessThan(root.get(field), (Comparable) value));
			break;
		default:
			throw new UnsupportedOperationException("Operation not supported");
		}
	}

	public Predicate getPredicate() {
		return this.predicate;
	}
}
