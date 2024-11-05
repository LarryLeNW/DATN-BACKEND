package com.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.blog.BlogCreationRequest;
import com.backend.dto.request.blog.BlogUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Blog;
import com.backend.repository.BlogRepository;
import com.backend.service.BlogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/blogs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BlogController {

	BlogService blogServices;
	
	@Autowired
	ObjectMapper objectMapper;

	@GetMapping
	public ApiResponse<PagedResponse<BlogResponse>> getAll(
	        @RequestParam(defaultValue = "1") @Min(value = 1, message = "page param be greater than 0") int page,
	        @RequestParam(defaultValue = "10") @Min(value = 1, message = "limit param be greater than 0") int limit,
	        @RequestParam(required = false) String sort, 
	        @RequestParam(required = false) String[] search) {

	    PagedResponse<BlogResponse> pagedResponse = blogServices.getBlogs(page, limit, sort, search);
	    return ApiResponse.<PagedResponse<BlogResponse>>builder().result(pagedResponse).build();
	}



	@PostMapping
	public ApiResponse<BlogResponse> createBlog(@RequestParam String blogData,
			 @RequestPart("images") List<MultipartFile> images) throws JsonMappingException, JsonProcessingException {

		BlogCreationRequest request = objectMapper.readValue(blogData, BlogCreationRequest.class);
		System.out.println(request);

		return ApiResponse.<BlogResponse>builder().result(blogServices.createBlog(request, images)).build();
	}


	
	@PutMapping("/{blogId}")
	ApiResponse<BlogResponse> updateBlog(@PathVariable Integer blogId, @RequestParam(required = false) String blogData,
			 @RequestPart("images") List<MultipartFile> images) throws JsonMappingException, JsonProcessingException {
			
		BlogUpdateRequest blogRequest = null; 
		
		if(blogData != null) {
			blogRequest = objectMapper.readValue(blogData, BlogUpdateRequest.class);
		}

		return ApiResponse.<BlogResponse>builder().result(blogServices.updateBlog(blogRequest, images, blogId)).build();
	}

	@DeleteMapping("/{blogId}")
	public ApiResponse<String> deleteBlog(@PathVariable Integer blogId) {
		blogServices.deleteBlog(blogId);
		return ApiResponse.<String>builder().result("delete success blog with id of: " + blogId).build();
	}
	@GetMapping("/{blogId}")
	ApiResponse<BlogResponse> getBlogById(@PathVariable Integer blogId) {
		return ApiResponse.<BlogResponse>builder().result(blogServices.getBlogById(blogId)).build();
	}

}
