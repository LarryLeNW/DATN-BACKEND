package com.backend.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.entity.Permission;
import com.backend.repository.PermissionRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataSeeder {

	@Autowired
	PermissionRepository permissionRespository;

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

		log.warn("End generate data");
	}
}