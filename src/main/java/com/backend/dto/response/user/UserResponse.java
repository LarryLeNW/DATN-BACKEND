package com.backend.dto.response.user;

import java.time.LocalDate;
import java.util.Set;

import com.backend.constant.Type.LoginType;
import com.backend.constant.Type.UserStatusType;
import com.backend.dto.response.auth.RoleResponse;
import com.backend.entity.Address;
import com.backend.entity.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String phone_number;
    String email;
    int points; 
    UserStatusType status; 
    LoginType login_type; 
    Role role;
    Set<Address> address;
}
