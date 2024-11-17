package com.backend.dto.response.auth;

import java.util.List;
import java.util.Set;

import com.backend.constant.Type.UserStatusType;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.User;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
	String name;
	String description;
	List<ModuleDTO> modules;
	List<UserDTO> users;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ModuleDTO {
		Long id;
		String name;
		List<PermissionDTO> permissions;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PermissionDTO {
		Long id;
		String name;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserDTO {
		String id;
		String username;
		String avatar;
		String email;
		UserStatusType status;
	}
	
	
	

}
