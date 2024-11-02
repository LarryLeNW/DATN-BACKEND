package com.backend.service;

import com.backend.entity.Role;
import com.backend.entity.RoleModulePermission;
import com.backend.mapper.RoleMapper;
import com.backend.repository.PermissionRepository;
import com.backend.repository.user.ModuleRepository;
import com.backend.repository.user.RoleRepository;

import jakarta.transaction.Transactional;

import com.backend.dto.request.auth.Role.ModuleDTO;
import com.backend.dto.request.auth.Role.RoleCreationRequest;
import com.backend.entity.Module;
import com.backend.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    
    public List<Role> getAll() {
    	return roleRepository.findAll();
    }
}
