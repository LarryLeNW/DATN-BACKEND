package com.backend.service;

import org.springframework.stereotype.Service;

import com.backend.dto.request.comment.CommentCreationRequest;
import com.backend.dto.response.comment.CommentResponse;
import com.backend.entity.Blog;
import com.backend.entity.CategoryBlog;
import com.backend.entity.Comment;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.CategoryMapper;
import com.backend.mapper.CommentMapper;
import com.backend.repository.BlogRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.CommentRepository;
import com.backend.repository.UserRepository;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentService {
	
	CommentRepository commentRepository;
	
	EntityManager entityManager;

	BlogRepository blogRepository;
	
	UserRepository userRepository;
	
	CommentMapper  commentMapper;
 	
	public CommentResponse createComment(CommentCreationRequest request) {
		
		Comment comment = new Comment();
		
		if (request.getBlogId() != 0) {
			Blog blog = blogRepository.findById(request.getBlogId())
					.orElseThrow(() -> new AppException(ErrorCode.CATEGORYBLOG_NOT_EXISTED));
			comment.setBlog(blog);
		}

		if (request.getUserId() != null) {
			User user = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			comment.setUser(user);
		}
		
		comment.setContent(request.getContent());
		
		return commentMapper.toCommentResponse(commentRepository.save(comment));
		
	}

}
