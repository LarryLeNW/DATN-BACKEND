package com.backend.dto.request.auth.Role;

import java.util.List;

import com.backend.entity.Permission;

import lombok.Data;

@Data
public class ModuleDTO {
	private Long id;
	private List<Long> permissions;
}
