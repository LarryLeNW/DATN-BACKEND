package com.backend.service;

import java.util.HashSet;
import java.util.List;

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
    
    
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(ProductCreationRequest request) {
    	Product product = productMapper.toProduct(request); 
    	
    	   // Find category, throw CATEGORY_NOT_FOUND if not found
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        
        // Find brand, throw BRAND_NOT_FOUND if not found
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
        
          product.setCategory(category);
          product.setBrand(brand);
          try {
              product = productRepository.save(product);
          } catch (DataIntegrityViolationException exception) {
              throw new AppException(ErrorCode.PRODUCT_EXISTED);
          }
          
    	return product ;
    }

    
}
