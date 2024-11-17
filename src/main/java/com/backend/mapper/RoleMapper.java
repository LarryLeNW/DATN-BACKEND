package com.backend.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.dto.request.auth.Role.ModuleDTO;
import com.backend.dto.request.auth.Role.RoleCreationRequest;
import com.backend.dto.response.auth.RoleResponse;
import com.backend.entity.Role;
import com.backend.entity.RoleModulePermission;

import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "roleModulePermissions", ignore = true)
    Role toRole(RoleCreationRequest request);

    @Mapping(source = "roleModulePermissions", target = "modules")
    @Mapping(source = "users", target = "users")
    RoleResponse toRoleResponse(Role role);

    default List<RoleResponse.ModuleDTO> toModulesDTO(List<RoleModulePermission> roleModulePermissions) {
        Map<com.backend.entity.Module, List<RoleModulePermission>> moduleMap = roleModulePermissions.stream()
            .collect(Collectors.groupingBy(RoleModulePermission::getModule));

        return moduleMap.entrySet().stream()
            .map(entry -> {
                com.backend.entity.Module module = entry.getKey();
                List<RoleModulePermission> permissionsForModule = entry.getValue();

                List<RoleResponse.PermissionDTO> permissions = permissionsForModule.stream()
                    .map(rmp -> new RoleResponse.PermissionDTO(
                        rmp.getPermission().getId(),
                        rmp.getPermission().getName()
                    ))
                    .toList();

                return new RoleResponse.ModuleDTO(
                    module.getId(),
                    module.getName(),
                    permissions
                );
            })
            .toList();
    }

}
