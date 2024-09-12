package com.backend.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.dto.request.UserCreationRequest;
import com.backend.dto.request.UserUpdateRequest;
import com.backend.dto.response.UserResponse;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.UserMapper;
import com.backend.repository.ProductRepository;
import com.backend.repository.RoleRepository;
import com.backend.repository.UserRepository;
import com.backend.constant.PredefinedRole;
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
    ProductRepository productRepository;
   
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    
}
