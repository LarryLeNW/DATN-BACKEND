package com.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Brand;
import com.backend.entity.Cart;
import com.backend.entity.Category;
import com.backend.entity.Order;
import com.backend.entity.Product;
import com.backend.entity.Sku;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BrandMapper;
import com.backend.mapper.CartMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.SkuRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.product.ProductRepository;
import com.backend.repository.user.CartRespository;
import com.backend.repository.user.UserRepository;
import com.backend.specification.CartSpecification;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
	CartMapper cartMapper;
	
	public PagedResponse<CartDetailResponse> getAll(Map<String, String> params) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    System.out.println("auth: "+auth);
	    String roleUser = auth.getAuthorities().iterator().next().toString();
	    String idUser = auth.getName(); 
	    
	    int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
	    int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
	    String sortField = params.getOrDefault("sortBy", "id");
	    String orderBy = params.getOrDefault("orderBy", "asc");

	    Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
	    Sort sort = Sort.by(direction, sortField);
	    Pageable pageable = PageRequest.of(page, limit, sort);

	    Specification<Cart> spec = Specification.where(null);

	    if ("ROLE_USER".equals(roleUser)) {
	        spec = spec.and(CartSpecification.belongsToUser(idUser));
	    }

	    if (params.containsKey("quantity")) {
	        Double quantity = Double.parseDouble(params.get("quantity"));
	        spec = spec.and(CartSpecification.hasQuantity(quantity));
	    }

	    Page<Cart> cartPage = cartRepository.findAll(spec, pageable);
	    List<CartDetailResponse> cartResponses = cartPage.getContent().stream()
	            .map(cartMapper::toCartDetailResponse)
	            .collect(Collectors.toList());

	    return new PagedResponse<>(cartResponses, page + 1, cartPage.getTotalPages(), cartPage.getTotalElements(), limit);
	}


	public PagedResponse<CartDetailResponse> create(CartCreationRequest request) {
		String idUser = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

		Sku sku = skuRepository.findById(request.getSkuId())
				.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));
		
		Cart cartUpdate = cartRepository.findOneCartByUserAndProductAndSku(user, product, sku) ; 

		if (cartUpdate == null)
		{
			cartUpdate = new Cart();
			cartUpdate.setUser(user);
			cartUpdate.setProduct(product);
			cartUpdate.setSku(sku);
			cartUpdate.setQuantity(request.getQuantity());
		}else {
			cartUpdate.setQuantity(cartUpdate.getQuantity() + request.getQuantity());
		}

		cartRepository.save(cartUpdate); 
		
		return this.getAll(new HashMap<>());
	}

	public void delete(Long cartId) {
		cartRepository.deleteById(cartId);
	}
	
	
}
