package com.backend.dto.request.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.entity.Role;
import com.backend.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
	@Size(min = 4, message = "USERNAME MINSIZE 4")
	String username;

	@NotNull
	@Size(min = 6, message = "INVALID_PASSWORD")
	String password;

	@NotNull
	@Email
	String email;
	
	String phone_number;

	LoginType login_type = LoginType.DEFAULT;;

	UserStatusType status = UserStatusType.INACTIVE;
	
	Role role;
}
