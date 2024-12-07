package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.dto.request.user.UserCreationRequest;
import com.backend.dto.request.user.UserUpdateRequest;
import com.backend.dto.response.user.TopReactUser;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.User;
import com.backend.service.AuthenticationService;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreationRequest userRequest);

    TopReactUser toTopReactUser(User user);
    
    default UserResponse toUserResponse(User user) {
        if (user == null) return null;

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

    @Mapping(target = "id", ignore = true) 
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
