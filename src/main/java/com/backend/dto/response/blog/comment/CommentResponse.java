package com.backend.dto.response.blog.comment;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.dto.response.blog.reply.ReplyResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {

	int commentId;
	String content;
	int blog_id;
	String user_id;
	String userName;
	String avatar;
	List<ReplyResponse> replyResponse; 
	LocalDateTime createdAt;
	LocalDateTime updateAt;
}
