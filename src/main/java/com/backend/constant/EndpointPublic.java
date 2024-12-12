package com.backend.constant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

public class EndpointPublic {

	public static final String[] PUBLIC_GET_ENDPOINTS = { "/api/product/**", "/api/categories", "/api/brands",
			"/api/blogs/**", "/api/auth/**", "/ws/**", "/api/messages", "/api/comments/blog/**", "/api/questions",
			"/api/users/top-reactions"  , "/api/payment/vn-pay/**" , "/api/payment/rental/vn-pay/**"};
	public static final String[] PUBLIC_POST_ENDPOINTS = { "/api/product", "/api/auth/**", "/api/users",
			"/api/categories", "/api/brands", "/ws/**", "/api/comments/blog/**", "/api/payment/**" };
	public static final String[] PUBLIC_DELETE_ENDPOINTS = { "/api/users" };
	public static final String[] PUBLIC_PUT_ENDPOINTS = {};

}
