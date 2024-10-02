package com.backend.repository.product;

import com.backend.entity.AttributeProduct;
import com.backend.entity.VariantProduct;
import com.backend.mapper.ProductMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.ProductRepository;
import com.backend.service.ProductService;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository 
@AllArgsConstructor
public class ProductQueryRepository<T> {

    private EntityManager entityManager;

    public CriteriaQuery<T> buildSearchQuery(Class<T> entityClass, String[] search, String sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        Join<T, VariantProduct> variantJoin = root.join("variant_product", JoinType.LEFT);
        Join<VariantProduct, AttributeProduct> attributeJoin = variantJoin.join("attributes", JoinType.LEFT);

        Predicate predicate = criteriaBuilder.conjunction();
        if (search != null) {
            List<SearchType> criteriaList = parseSearchCriteria(search);
            ConsumerCondition queryConsumer = new ConsumerCondition(criteriaBuilder, predicate, root, variantJoin, attributeJoin);
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
        }
        query.where(predicate);

        if (StringUtils.hasLength(sortBy)) {
            query.orderBy(buildSortOrder(root, sortBy, criteriaBuilder));
        }

        return query;
    }



	List<SearchType> parseSearchCriteria(String[] search) {
		List<SearchType> criteriaList = new ArrayList<>();
		Pattern pattern = Pattern.compile("(\\w+?)(=|>|<|:)(.*)");

		for (String s : search) {
			Matcher matcher = pattern.matcher(s);
			if (matcher.find()) {
				criteriaList.add(new SearchType(matcher.group(1), matcher.group(2), matcher.group(3)));
			}
		}
		return criteriaList;
	}

	Order buildSortOrder(Root<T> root, String sortBy ,CriteriaBuilder criteriaBuilder) {
		Pattern pattern = Pattern.compile("(\\w+?):(asc|desc)");
		Matcher matcher = pattern.matcher(sortBy);
		if (matcher.find()) {
			String columnName = matcher.group(1);
			String sortDirection = matcher.group(2);
			if ("desc".equalsIgnoreCase(sortDirection)) {
				return criteriaBuilder.desc(root.get(columnName));
			} else {
				return criteriaBuilder.asc(root.get(columnName));
			}
		}
		return criteriaBuilder.asc(root.get("id"));
	}
	
	public CriteriaQuery<Long> buildCountQuery(Class<T> entityClass, String[] search) {
	    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
	    Root<T> root = countQuery.from(entityClass);
	    
	    // Join Product với VariantProduct
        Join<T, VariantProduct> variantJoin = root.join("variant_product", JoinType.LEFT);

        // Join VariantProduct với AttributeProduct
        Join<VariantProduct, AttributeProduct> attributeJoin = variantJoin.join("attributes", JoinType.LEFT);

	    Predicate predicate = criteriaBuilder.conjunction();
	    if (search != null) {
	        List<SearchType> criteriaList = parseSearchCriteria(search);
	        ConsumerCondition queryConsumer = new ConsumerCondition(criteriaBuilder, predicate, root,variantJoin, attributeJoin);
	        criteriaList.forEach(queryConsumer);
	        predicate = queryConsumer.getPredicate();
	    }

	    countQuery.select(criteriaBuilder.count(root)).where(predicate);
	    
	    return countQuery;
	}


}
