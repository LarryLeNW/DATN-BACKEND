package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.User;
import com.backend.service.AuthenticationService;

@Mapper(componentModel = "spring")
public class UserMapper {
    public static User toUser(UserCreationRequest userRes) {
        return User.builder()
    	        .username(userRes.getUsername())
    	        .email(userRes.getEmail())
    	        .status(userRes.getStatus())
    	        .password(userRes.getPassword())
    	        .login_type(userRes.getLogin_type())
    	        .build();
    }

	public static UserResponse toUserResponse(User user) {
	    String role = AuthenticationService.buildScope(user);
	    return UserResponse.builder()
	        .id(user.getId())
	        .username(user.getUsername())
	        .phone_number(user.getPhone_number())
	        .email(user.getEmail())
	        .points(user.getPoints())
	        .status(user.getStatus())
	        .login_type(user.getLogin_type())
	        .address(user.getAddress())
	        .avatar(user.getAvatar())
	        .role(role)
	        .build();
	}

//    @Mapping(target = "role", ignore = true)
//    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
