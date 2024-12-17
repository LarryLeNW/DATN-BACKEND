package com.backend.dto.response.user;

import lombok.Data;

@Data
public class TopReactUser {
	private String id;
	private String username;
	private String email;
	private String avatar;
	private Long totalReactions;
}
