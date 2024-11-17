package com.backend.service;

import com.backend.entity.Role;
import com.backend.entity.RoleModulePermission;
import com.backend.entity.User;
import com.backend.mapper.RoleMapper;
import com.backend.repository.PermissionRepository;
import com.backend.repository.user.ModuleRepository;
import com.backend.repository.user.RoleRepository;

import jakarta.transaction.Transactional;

import com.backend.dto.request.auth.Role.ModuleDTO;
import com.backend.dto.request.auth.Role.RoleCreationRequest;
import com.backend.dto.response.auth.RoleResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.Module;
import com.backend.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired 
    private ModuleRepository moduleRepository;
    
    @Autowired
    private RoleMapper roleMapper; 
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Transactional
    public Role create(RoleCreationRequest request) {
        Role role = roleMapper.toRole(request);
        role = roleRepository.save(role);

        if (request.getModules() != null) {
            for (ModuleDTO moduleDTO : request.getModules()) {
                Module module = moduleRepository.findById(moduleDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Module not found with ID: " + moduleDTO.getId()));

                for (Long permissionId : moduleDTO.getPermissions()) {
                    Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + permissionId));

                    RoleModulePermission roleModulePermission = new RoleModulePermission();
                    roleModulePermission.setRole(role);
                    roleModulePermission.setModule(module);
                    roleModulePermission.setPermission(permission);
                    
                    if (role.getRoleModulePermissions() == null) {
                        role.setRoleModulePermissions(new ArrayList<>());
                    }
                    role.getRoleModulePermissions().add(roleModulePermission);
                }
            }
        }

        return roleRepository.save(role);
    }
    
    public PagedResponse<RoleResponse> getAll(Map<String, String> params) {
    	int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);
		Page<Role> rolePage = roleRepository.findAll(pageable);
		List<RoleResponse> roleResponses = rolePage.getContent().stream().map(roleMapper::toRoleResponse)
				.collect(Collectors.toList());
		
		return new PagedResponse<>(roleResponses, page + 1, rolePage.getTotalPages(), rolePage.getTotalElements(),
				limit);
    }
}
