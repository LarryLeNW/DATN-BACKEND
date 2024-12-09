//package com.backend.configuration;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import com.backend.constant.PredefinedRole;
//import com.backend.constant.Type.UserStatusType;
//import com.backend.entity.*;
//import com.backend.entity.Module;
//import com.backend.repository.*;
//import com.backend.repository.user.*;
//
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//public class DataSeeder {
//
//	@Autowired
//	PermissionRepository permissionRespository;
//
//	@Autowired
//	RoleRepository roleRepository;
//
//	@Autowired
//	UserRepository userRepository;
//
//	@Autowired
//	ModuleRepository moduleRepository;
//
//	@Autowired
//	PasswordEncoder passwordEncoder;
//
//	List<Permission> permissions = new ArrayList<>(Arrays.asList(new Permission((long) 1, "CREATE"),
//			new Permission((long) 2, "VIEWALL"), new Permission((long) 3, "VIEW"), new Permission((long) 4, "UPDATE"),
//			new Permission((long) 5, "DELETE")));
//
//	List<Module> modules = new ArrayList<>(Arrays.asList(new Module((long) 1, "USER"), new Module((long) 2, "PRODUCT"),
//			new Module((long) 3, "BLOG"), new Module((long) 4, "PRODUCT_CATEGORY")));
//
//	List<User> users = new ArrayList<>(
//			Arrays.asList(new User("Nguyễn Văn A", passwordEncoder.encode("123456"), "nguyenvana@gmail.com"),
//					new User("Nguyễn Văn B", passwordEncoder.encode("123456"), "nguyenvanb@gmail.com"),
//					new User("Nguyễn Văn C", passwordEncoder.encode("123456"), "nguyenvanc@gmail.com"),
//					new User("Nguyễn Văn D", passwordEncoder.encode("123456"), "nguyenvand@gmail.com"),
//					new User("Nguyễn Văn E", passwordEncoder.encode("123456"), "nguyenvane@gmail.com"),
//					new User("Nguyễn Văn F", passwordEncoder.encode("123456"), "nguyenvanf@gmail.com"),
//					new User("Nguyễn Văn G", passwordEncoder.encode("123456"), "nguyenvang@gmail.com")));
//
////	@PostConstruct
//	public void seed() {
//		log.info("Start generate data");
//
//		// create permission
//
//		if (permissionRespository.count() == 0) {
//			permissionRespository.saveAll(permissions);
//		}
//
//		moduleRepository.saveAll(modules);
//
//		// create user role
//		Role userRole = roleRepository.findByName(PredefinedRole.USER_NAME);
//		if (userRole == null)
//			userRole = roleRepository.save(new Role(PredefinedRole.USER_NAME, "This role for customer"));
//
//		for (User customer : users) {
//			customer.setRole(userRole);
//			userRepository.save(customer);
//		}
//
//		// create admin role
//		Role adminRole = roleRepository.findByName(PredefinedRole.ADMIN_NAME);
//		if (adminRole == null)
//			adminRole = roleRepository.save(new Role(PredefinedRole.ADMIN_NAME, "This role for superadmin..."));
//
//		// create admin
//		User admin = new User();
//		admin.setEmail("superadmin@gmail.com");
//		admin.setUsername("Superadmin");
//		admin.setPassword(passwordEncoder.encode("123456"));
//		admin.setStatus(UserStatusType.ACTIVED);
//		admin.setRole(adminRole);
//		userRepository.save(admin);
//
//		log.info("End generate data");
//	}
//}