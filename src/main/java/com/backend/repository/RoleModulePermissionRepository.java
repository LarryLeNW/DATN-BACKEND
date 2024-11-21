package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Blog;
import com.backend.entity.RoleModulePermission;
import com.backend.entity.Role;


public interface RoleModulePermissionRepository extends JpaRepository<RoleModulePermission, Integer> {
	void deleteAllByRole(Role role);
}
