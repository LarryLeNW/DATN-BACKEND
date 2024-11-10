package com.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.dto.request.brand.BrandUpdateRequest;
import com.backend.dto.request.cart.CartCreationRequest;
import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Brand;
import com.backend.entity.Cart;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.entity.Sku;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BrandMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.SkuRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.product.ProductRepository;
import com.backend.repository.user.CartRespository;
import com.backend.repository.user.UserRepository;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartService {

	BrandRepository brandRepository;
	BrandMapper brandMapper;
	EntityManager entityManager;
	UploadFile uploadFile;

	CartRespository cartRepository;
	SkuRepository skuRepository;
	UserRepository userRepository;
	ProductRepository productRepository;

	public PagedResponse<Brand> getBrands(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Brand> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Brand> query = customSearchService.buildSearchQuery(Brand.class, search, sort);

		List<Brand> brands = entityManager.createQuery(query).setFirstResult((page - 1) * limit).setMaxResults(limit)
				.getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Brand.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(brands, page, totalPages, totalElements, limit);
	}

	public Cart create(CartCreationRequest request) {
		log.info(request.toString());
		String idUser = SecurityContextHolder.getContext().getAuthentication().getName();
		
		User user = 
				userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)); 
		
		Sku sku = skuRepository.findById(request.getSkuId())
				.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));
		

		Cart newCart = new Cart();
		newCart.setUser(user);
		newCart.setProduct(product);
		newCart.setSku(sku);

		log.info("new cart " + newCart.toString());

		return null;
	}

	public Brand updateBrand(Long brandId, BrandUpdateRequest request, MultipartFile image) {
		Brand brand = brandRepository.findById(brandId)
				.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

		if (request != null) {
			Helpers.updateFieldEntityIfChanged(request.getName(), brand.getName(), brand::setName);
			Helpers.updateFieldEntityIfChanged(request.getDescription(), brand.getDescription(), brand::setDescription);

			if (request.getName() != null)
				brand.setSlug(Helpers.toSlug(request.getName()));
		}

		if (image != null) {
			String imageUrl = uploadFile.saveFile(image, "brandTest");
			brand.setImage(imageUrl);
		}

		return brandRepository.save(brand);
	}

	public void deleteBrand(Long brandId) {
		brandRepository.deleteById(brandId);
	}

}
