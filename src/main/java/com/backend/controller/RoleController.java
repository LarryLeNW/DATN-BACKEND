package com.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.auth.Role.RoleCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.auth.RoleResponse;
import com.backend.entity.Role;
import com.backend.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<Role> create(@RequestBody RoleCreationRequest request) {
        return ApiResponse.<Role>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<Role>> getAll() {
        return ApiResponse.<List<Role>>builder()
                .result(roleService.getAll())
                .build();
    }

//    @DeleteMapping("/{role}")
//    ApiResponse<Void> delete(@PathVariable String role) {
//        roleService.delete(role);
//        return ApiResponse.<Void>builder().build();
//    }
}
