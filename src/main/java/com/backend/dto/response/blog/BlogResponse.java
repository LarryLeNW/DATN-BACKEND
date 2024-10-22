	package com.backend.dto.response.blog;
	
	import java.time.LocalDateTime;
	
	import com.backend.entity.CategoryBlog;
	import com.backend.entity.User;
	
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
		String images;
		String user_id;
		String categoryBlog_id;
		LocalDateTime createdAt;
	
	}
