package com.backend.constant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

public class EndpointConstants {

	public static final String[] PUBLIC_GET_ENDPOINTS = { "/api/product", "/api/categories", "/api/brands", "/api/blogs", "/api/auth/**","/ws/**" };
	public static final String[] PUBLIC_POST_ENDPOINTS = { "/api/product", "/api/auth/**", "/api/users", "/api/categories", "/api/brands","/ws/**"  };
	public static final String[] PUBLIC_DELETE_ENDPOINTS = { "/api/users" };
	public static final String[] PUBLIC_PUT_ENDPOINTS = {};

}
