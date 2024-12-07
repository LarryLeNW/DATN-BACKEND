package com.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.user.TopReactUser;
import com.backend.dto.response.user.UserResponse;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.UserMapper;
import com.backend.repository.user.RoleRepository;
import com.backend.repository.user.UserRepository;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import com.backend.constant.PredefinedRole;
import com.backend.constant.Type.UserStatusType;
import com.backend.entity.Cart;
import com.backend.entity.Product;
import com.backend.entity.QuestionReaction;
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
public class UserService {
	UserRepository userRepository;
	RoleRepository roleRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;
	EntityManager entityManager;
	UploadFile uploadFile;

	public UserResponse createUser(UserCreationRequest request) {

		User userFound = userRepository.findByEmail(request.getEmail());
		if (userFound != null) {
			throw new RuntimeException("Email has already been used.");
		}

		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		Role roleUser = roleRepository.findById(request.getRole().getId())
				.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

		user.setRole(roleUser);

		try {
			user = userRepository.save(user);
		} catch (DataIntegrityViolationException exception) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}

		return userMapper.toUserResponse(user);
	}

	public UserResponse getMyInfo() {
		User user = userRepository.findByUsername("test")
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		return userMapper.toUserResponse(user);
	}

	@PreAuthorize("hasRole('ROLE_SUPERADMIN') or returnObject.id == authentication.name")
	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		System.out.println("UserUpdateRequest :" + request.toString());
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		if (request.getEmail() != null) {
			User checkExitByEmail = userRepository.findByEmail(request.getEmail());
			if (checkExitByEmail != null && checkExitByEmail.getId() != user.getId())
				throw new RuntimeException("Email has already been used.");
		}

		userMapper.updateUser(user, request);

		if (request.getPassword() != null)
			user.setPassword(passwordEncoder.encode(request.getPassword()));

		return userMapper.toUserResponse(userRepository.save(user));
	}

	public UserResponse updateInfoUser(String userId, UserUpdateRequest request, MultipartFile avatar) {
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		if (avatar != null) {
			String avatarUrl = uploadFile.saveFile(avatar, "brandTest");
			System.out.println("hihiih,"+avatarUrl);
			user.setAvatar(avatarUrl);
		}
		
		if (request.getUsername() != null && !request.getUsername().isEmpty()) {
			user.setUsername(request.getUsername());
		}

		if (request.getPassword() != null && !request.getPassword().isEmpty()) {
		    user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		if (request.getEmail() != null && !request.getEmail().isEmpty()) {
			User existingUser = userRepository.findByEmail(request.getEmail());
			if (existingUser != null && !existingUser.getId().equals(user.getId())) {
				throw new RuntimeException("Email has already been used.");
			}
			user.setEmail(request.getEmail());
		}

		if (request.getPhone_number() != null && !request.getPhone_number().isEmpty()) {
			user.setPhone_number(request.getPhone_number());
		}

		if (request.getRole() != null) {
			user.setRole(request.getRole());
		}


		return userMapper.toUserResponse(userRepository.save(user));
	}

	@PreAuthorize("hasRole('ROLE_SUPERADMIN') or hasAuthority('USER_DELETE')")
	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	@PreAuthorize("hasRole('ROLE_SUPERADMIN') or hasAuthority('USER_VIEWALL')")
	public PagedResponse<UserResponse> getUsers(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);

		Specification<User> spec = Specification.where(null);
		if (params.containsKey("keyword")) {
			String keyword = params.get("keyword").toLowerCase();
			spec = spec.and((root, query, builder) -> builder.or(
					builder.like(builder.lower(root.get("username")), "%" + keyword + "%"),
					builder.like(builder.lower(root.get("id")), "%" + keyword + "%"),
					builder.like(builder.lower(root.get("email")), "%" + keyword + "%"),
					builder.like(builder.lower(root.get("avatar")), "%" + keyword + "%"),
					builder.like(builder.lower(root.get("phone_number")), "%" + keyword + "%")));
		}

		if (params.containsKey("role")) {
			String roleId = params.get("role");
			spec = spec.and((root, query, builder) -> builder.equal(root.get("role").get("id"), roleId));
		}

		if (params.containsKey("status")) {
			String status = params.get("status");
			spec = spec
					.and((root, query, builder) -> builder.equal(root.get("status"), UserStatusType.valueOf(status)));
		}

		Page<User> userPage = userRepository.findAll(spec, pageable);

		List<UserResponse> userResponses = userPage.getContent().stream().map(userMapper::toUserResponse)
				.collect(Collectors.toList());

		return new PagedResponse<>(userResponses, page + 1, userPage.getTotalPages(), userPage.getTotalElements(),
				limit);
	}

	public UserResponse getUser(String id) {
		return userMapper.toUserResponse(
				userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
	}
	
	public List<TopReactUser> getTopUsersWithMostReactions() {
        Pageable pageable = PageRequest.of(0, 10);  

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<QuestionReaction> root = cq.from(QuestionReaction.class);

        cq.multiselect(root.get("user").get("id"), cb.count(root));
        cq.groupBy(root.get("user").get("id"));
        cq.orderBy(cb.desc(cb.count(root)));  

        Query query = entityManager.createQuery(cq);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Object[]> results = query.getResultList();

        return results.stream()
            .map(result -> {
                String userId = (String) result[0];
                Long totalReactions = (Long) result[1] ;
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                TopReactUser response = userMapper.toTopReactUser(user);
                response.setTotalReactions(totalReactions); 
                return response;
            })
            .collect(Collectors.toList());
    }
	
	public boolean changePassword(String email, String oldPassword, String newPassword) {
	    User user = userRepository.findByEmail(email);
	    
	    if (user != null) {
	        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
	            user.setPassword(passwordEncoder.encode(newPassword));
	            userRepository.save(user);
	            return true;
	        } else {
	            return false;
	        }
	    }
	    return false;
	}
}
