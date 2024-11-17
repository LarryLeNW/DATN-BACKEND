package com.backend.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.constant.PredefinedRole;
import com.backend.entity.*;
import com.backend.repository.*;
import com.backend.repository.user.RoleRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataSeeder {

	@Autowired
	PermissionRepository permissionRespository;

	@Autowired
	RoleRepository roleRepository;

	List<Permission> permissions = new ArrayList<>(Arrays.asList(new Permission((long) 1, "CREATE"),
			new Permission((long) 2, "VIEWALL"), new Permission((long) 3, "VIEW"), new Permission((long) 4, "UPDATE"),
			new Permission((long) 5, "DELETE")));
	
	

	@PostConstruct
	public void seed() {
		log.warn("Start generate data");

		// create permission
		
		if (permissionRespository.count() == 0) {
			permissionRespository.saveAll(permissions);
		}

		// create role
		Role userRole = roleRepository.findByName(PredefinedRole.USER_NAME);
		if(userRole == null) 
			roleRepository.save(new Role(PredefinedRole.USER_NAME, "This role for customer")); 
		
		Role adminRole = roleRepository.findByName(PredefinedRole.ADMIN_NAME);
		if(adminRole == null) 
			roleRepository.save(new Role(PredefinedRole.ADMIN_NAME, "This role for customer")); 
		
		log.warn("End generate data");
	}
}