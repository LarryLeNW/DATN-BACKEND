package com.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.backend.dto.request.auth.Role.RoleCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.auth.RoleResponse;
import com.backend.dto.response.common.PagedResponse;
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
    ApiResponse<PagedResponse<RoleResponse>> getAll(@RequestParam Map<String, String> params) {
        return ApiResponse.<PagedResponse<RoleResponse>>builder()
                .result(roleService.getAll(params))
                .build();
    }

//    @DeleteMapping("/{role}")
//    ApiResponse<Void> delete(@PathVariable Long role) {
//        roleService.delete(role);
//        return ApiResponse.<Void>builder().build();
//    }
}
