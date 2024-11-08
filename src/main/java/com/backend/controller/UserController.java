package com.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
	UserService userService;

	@PostMapping
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
		return ApiResponse.<UserResponse>builder().result(userService.createUser(request)).build();
	}


    @PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('USER_VIEWALL')")
	@GetMapping
	ApiResponse<List<UserResponse>> getUsers() {
		var context = SecurityContextHolder.getContext();
		var authentication = context.getAuthentication();
		

		if (authentication != null && authentication.isAuthenticated()) {
			log.info(authentication.getAuthorities().toString());
		} else {
			log.info("No authenticated user found or request does not require authentication.");
		}

		return ApiResponse.<List<UserResponse>>builder().result(userService.getUsers()).build();
	}

	@GetMapping("/{userId}")
	ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {

		return ApiResponse.<UserResponse>builder().result(userService.getUser(userId)).build();
	}

	@GetMapping("/my-info")
	ApiResponse<UserResponse> getMyInfo() {
		return ApiResponse.<UserResponse>builder().result(userService.getMyInfo()).build();
	}

	@DeleteMapping("/{userId}")
	ApiResponse<String> deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return ApiResponse.<String>builder().result("User has been deleted").build();
	}

	@PutMapping("/{userId}")
	ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
		return ApiResponse.<UserResponse>builder().result(userService.updateUser(userId, request)).build();
	}
}
