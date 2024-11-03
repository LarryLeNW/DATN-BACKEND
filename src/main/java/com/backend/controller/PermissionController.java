package com.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.auth.PermissionRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.auth.PermissionResponse;
import com.backend.entity.Permission;
import com.backend.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<Permission> create(@RequestBody Permission request) {
        return ApiResponse.<Permission>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<Permission>> getAll() {
        return ApiResponse.<List<Permission>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable Long permission) {
        permissionService.delete(permission);
        return ApiResponse.<Void>builder().build();
    }
}
