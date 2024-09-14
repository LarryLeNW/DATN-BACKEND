package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.dto.request.RoleRequest;
import com.backend.dto.response.RoleResponse;
import com.backend.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}