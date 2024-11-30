package com.backend.dto.request.user;

import java.time.LocalDate;
import java.util.List;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.entity.Role;
import com.backend.validator.DobConstraint;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
	@Size(min = 4, message = "USERNAME MINSIZE 4")
	String username;

	@Size(min = 6, message = "INVALID_PASSWORD")
	String password;

	@Email
	String email;
	
	String avatar;
	
	String phone_number;

	Role role;
}
