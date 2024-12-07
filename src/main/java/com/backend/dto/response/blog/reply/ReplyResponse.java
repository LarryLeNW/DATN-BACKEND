package com.backend.dto.response.blog.reply;

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
	String userComment;
	String userName;
	String parentReplyUserName;
	String avatar;
	int comment_id;
	String user_id;
	LocalDateTime createdAt;
	LocalDateTime updateAt;
    List<ReplyResponse> replies; 
}
