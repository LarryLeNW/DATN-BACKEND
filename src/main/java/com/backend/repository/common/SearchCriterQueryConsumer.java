package com.backend.repository.common;

import java.util.function.Consumer;

import jakarta.persistence.criteria.CriteriaBuilder;
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
public class SearchCriterQueryConsumer implements Consumer<SearchCriteria> {

	CriteriaBuilder builder; 
	Predicate predicate; 
	Root root ;
	
    @Override
    public void accept(SearchCriteria criteria) {
        String key = criteria.getKey();
        String operation = criteria.getOperation();
        Object value = criteria.getValue();

        switch (operation) {
            case "=": 
                predicate = builder.and(predicate, builder.equal(root.get(key), value));
                break;
            case ">":
                predicate = builder.and(predicate, builder.greaterThan(root.get(key), (Comparable) value));
                break;
            case "<":
                predicate = builder.and(predicate, builder.lessThan(root.get(key), (Comparable) value));
                break;
            case ":": 
            	predicate = builder.and(predicate, builder.like(root.get(key), "%" + value + "%"));
                break;
            default:
                throw new UnsupportedOperationException("Operation not supported");
        }
    }

}
