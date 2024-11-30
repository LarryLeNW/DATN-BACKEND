package com.backend.dto.response.blog;

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
public class BlogResponse {
	int blogId;
	String title;
	String content;
	String image;
	String user_id;
    String userName;  
	String categoryBlog_id;
	String categoryBlogName;
	LocalDateTime createdAt;

}
