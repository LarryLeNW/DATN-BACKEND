package com.backend.dto.response.reply;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyResponse {
           
	int replyId;
	String content;
	int comment_id;
	int user_id;
	LocalDateTime createdAt;
	LocalDateTime updateAt;
	
}
