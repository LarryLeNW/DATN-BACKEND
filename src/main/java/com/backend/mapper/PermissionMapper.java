package com.backend.mapper;

import org.mapstruct.Mapper;

import com.backend.dto.request.auth.PermissionRequest;
import com.backend.dto.response.auth.PermissionResponse;
import com.backend.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
