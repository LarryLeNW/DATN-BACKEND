package com.backend.dto.response.blog.comment;

import java.time.LocalDateTime;

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
	LocalDateTime createdAt;
	LocalDateTime updateAt;
}
