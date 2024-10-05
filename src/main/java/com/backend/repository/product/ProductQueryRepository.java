//package com.backend.repository.product;
//
//import com.backend.entity.AttributeProduct;
//import com.backend.entity.VariantProduct;
//import jakarta.persistence.*;
//import lombok.*;
//import jakarta.persistence.criteria.*;
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Slf4j
//@Repository
//@AllArgsConstructor
//public class ProductQueryRepository<T> {
//
//	private EntityManager entityManager;
//
//	public CriteriaQuery<T> buildSearchQuery(Class<T> entityClass, String[] search, String sortBy) {
//	    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//	    CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClass);
//	    Root<T> root = query.from(entityClass);
//
//	    // Join trực tiếp với AttributeProduct để chuẩn bị lọc theo thuộc tính
//	    Join<T, AttributeProduct> attributeJoin = root.join("attributes", JoinType.LEFT);
//
//	    Predicate predicate = criteriaBuilder.conjunction(); // Khởi tạo Predicate rỗng
//
//	    // Luôn join với attributes, và kiểm tra nếu search không null và không rỗng để lọc
//	    if (search != null && search.length > 0) {
//	        List<SearchType> criteriaList = parseSearchCriteria(search);
//	        ConsumerCondition queryConsumer = new ConsumerCondition(criteriaBuilder, predicate, root, attributeJoin);
//	        criteriaList.forEach(queryConsumer); // Áp dụng các điều kiện tìm kiếm
//	        predicate = queryConsumer.getPredicate();
//	    }
//
//	    query.where(predicate); // Đưa điều kiện vào câu truy vấn
//
//	    // Xử lý điều kiện sắp xếp nếu có
//	    if (StringUtils.hasLength(sortBy)) {
//	        query.orderBy(buildSortOrder(root, sortBy, criteriaBuilder));
//	    }
//
//	    return query;
//	}
//
//
//	List<SearchType> parseSearchCriteria(String[] search) {
//		List<SearchType> criteriaList = new ArrayList<>();
//		Pattern conditionPattern = Pattern.compile("(\\w+?)(=|>|<)([^&]+)");
//
//		for (String s : search) {
//			Matcher matcher = conditionPattern.matcher(s);
//			while (matcher.find()) {
//				criteriaList.add(new SearchType(matcher.group(1), matcher.group(2), matcher.group(3).trim()));
//			}
//		}
//		return criteriaList;
//	}
//
//	List<SearchType> parseAttributeCriteria(String attributes) {
//		List<SearchType> criteriaList = new ArrayList<>();
//		String[] pairs = attributes.split(",");
//		for (String pair : pairs) {
//			String[] keyValue = pair.split(":");
//			if (keyValue.length == 2) {
//				criteriaList.add(new SearchType(keyValue[0], "=", keyValue[1].trim()));
//			}
//		}
//		return criteriaList;
//	}
//
//	Order buildSortOrder(Root<T> root, String sortBy, CriteriaBuilder criteriaBuilder) {
//		Pattern pattern = Pattern.compile("(\\w+?):(asc|desc)");
//		Matcher matcher = pattern.matcher(sortBy);
//		if (matcher.find()) {
//			String columnName = matcher.group(1);
//			String sortDirection = matcher.group(2);
//			return "desc".equalsIgnoreCase(sortDirection) ? criteriaBuilder.desc(root.get(columnName))
//					: criteriaBuilder.asc(root.get(columnName));
//		}
//		return criteriaBuilder.asc(root.get("id"));
//	}
//
//	public CriteriaQuery<Long> buildCountQuery(Class<T> entityClass, String[] search) {
//	    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//	    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//	    Root<T> root = countQuery.from(entityClass); // Chỉ cần root cho thực thể chính
//
//	    Predicate predicate = criteriaBuilder.conjunction(); // Khởi tạo Predicate rỗng
//
//	    // Kiểm tra nếu search không null và không rỗng
//	    if (search != null && search.length > 0) {
//	        List<SearchType> criteriaList = parseSearchCriteria(search);
//	        ConsumerCondition queryConsumer = new ConsumerCondition(criteriaBuilder, predicate, root, null); // Không cần attributeJoin
//	        criteriaList.forEach(queryConsumer); // Áp dụng các điều kiện tìm kiếm
//	        predicate = queryConsumer.getPredicate();
//	    }
//
//	    countQuery.select(criteriaBuilder.count(root)).where(predicate); // Thêm điều kiện vào câu truy vấn đếm
//	    return countQuery;
//	}
//
//}
