package com.backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopOrderUser {
	private String userId;
	private String username;
	private Double totalPaymentAmount;
}
