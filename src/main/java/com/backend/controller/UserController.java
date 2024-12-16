package com.backend.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.blog.BlogUpdateRequest;
import com.backend.dto.request.user.ChangePasswordRequest;
import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.user.TopOrderUser;
import com.backend.dto.response.user.TopReactUser;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.Cart;
import com.backend.entity.User;
import com.backend.service.UserService;
import com.backend.utils.UploadFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private UploadFile uploadFile;

	@PostMapping
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
		return ApiResponse.<UserResponse>builder().result(userService.createUser(request)).build();
	}

	@GetMapping
	ApiResponse<PagedResponse<UserResponse>> getUsers(@RequestParam Map<String, String> params) {
		return ApiResponse.<PagedResponse<UserResponse>>builder().result(userService.getUsers(params)).build();
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
		System.out.println("chạy đến updateUser");
		return ApiResponse.<UserResponse>builder().result(userService.updateUser(userId, request)).build();
	}

	@PutMapping("/{id}/info")
	ApiResponse<UserResponse> updateInfoUser(@PathVariable String id, @RequestParam(required = false) String userData,
			@RequestParam(required = false) MultipartFile avatar) throws JsonMappingException, JsonProcessingException {

		System.out.println("chạy đến updateUserInfo");
		System.out.println("thoong tin : " + userData);

		UserUpdateRequest userRequest = null;

		if (userData != null) {
			userRequest = objectMapper.readValue(userData, UserUpdateRequest.class);
		}
		return ApiResponse.<UserResponse>builder().result(userService.updateInfoUser(id, userRequest, avatar)).build();
	}

	@PutMapping("/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
		try {
			boolean isChanged = userService.changePassword(request.getEmail(), request.getOldPassword(),
					request.getNewPassword());

			if (isChanged) {
				return ResponseEntity.ok("Thay đổi mật khẩu thành công");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Mật khẩu cũ không chính xác hoặc người dùng không tồn tại");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while changing the password");
		}
	}

	@GetMapping("/top-reactions")
	public List<TopReactUser> getTopUsersWithMostReactions() {
		return userService.getTopUsersWithMostReactions();
	}

	@GetMapping("/statistics/registrations")
	public ResponseEntity<?> getUserRegistrationsStatistics(@RequestParam(required = false) Integer day,
			@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year) {

		long count = userService.getUserRegistrationsByDate(day, month, year);
		return ResponseEntity.ok(Map.of("totalRegistrations", count));
	}

	@GetMapping("/statistics/status")
	public ResponseEntity<?> getUserStatisticsByStatus() {
		return ResponseEntity.ok(userService.getUserStatisticsByStatus());
	}

	@GetMapping("/statistics/roles")
	public ResponseEntity<?> getUserStatisticsByRole() {
		return ResponseEntity.ok(userService.getUserStatisticsByRole());
	}
	

    @GetMapping("/statistics/payment")
    public ResponseEntity<List<TopOrderUser>> getTop10UsersByPaymentAmount() {
        List<TopOrderUser> topUsers = userService.getTop10UsersByPaymentAmount();
        return ResponseEntity.ok(topUsers);
    }
}
