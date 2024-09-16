package com.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.UserResponse;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ProductMapper;
import com.backend.mapper.UserMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.RoleRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.common.SearchCriterQueryConsumer;
import com.backend.repository.common.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.backend.constant.PredefinedRole;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.entity.Role;
import com.backend.entity.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

	CategoryRepository categoryRepository;
	BrandRepository brandRepository;
	ProductRepository productRepository;
	ProductMapper productMapper;
	EntityManager entityManager;

	public List<Product> getProducts(int page, int limit, String sortBy, String... search) {
		List<SearchCriteria> criteriaList = new ArrayList<>();

		// format search criteria
		if (search != null) {
			for (String s : search) {
				Pattern pattern = Pattern.compile("(\\w+?)(=|>|<|:)(.*)");
				Matcher matcher = pattern.matcher(s);

				if (matcher.find())
					criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
			}
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = query.from(Product.class);

		// handle search condition
		Predicate predicate = criteriaBuilder.conjunction();
		SearchCriterQueryConsumer queryConsumer = new SearchCriterQueryConsumer(criteriaBuilder, predicate, root);
		criteriaList.forEach(queryConsumer);
		predicate = queryConsumer.getPredicate();
		query.where(predicate);

		return entityManager.createQuery(query).setFirstResult(page).setMaxResults(limit).getResultList();
	}

	public Product createProduct(ProductCreationRequest request) {
		Product product = productMapper.toProduct(request);

		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

		Brand brand = brandRepository.findById(request.getBrandId())
				.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

		product.setCategory(category);
		product.setBrand(brand);
		try {
			product = productRepository.save(product);
		} catch (DataIntegrityViolationException exception) {
			throw new AppException(ErrorCode.PRODUCT_EXISTED);
		}

		return product;
	}

}
