package com.backend.dto.request.auth.Role;

import java.util.List;
import java.util.Set;

import com.backend.entity.RoleModulePermission;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleCreationRequest {
	String name;
	String description;
	List<ModuleDTO> modules;
}


