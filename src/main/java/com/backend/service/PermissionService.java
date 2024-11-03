package com.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.PermissionRepository;
import com.backend.dto.request.auth.PermissionRequest;
import com.backend.dto.response.auth.PermissionResponse;
import com.backend.entity.Permission;
import com.backend.mapper.PermissionMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;

    public Permission create(Permission permission) {
        return permissionRepository.save(permission);
    }

    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    public void delete(Long permission) {
        permissionRepository.deleteById(permission);
    }
}
