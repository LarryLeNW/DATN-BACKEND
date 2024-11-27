package com.backend.service;

import java.util.ArrayList;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.backend.dto.request.blog.comment.CommentCreationRequest;
import com.backend.dto.request.blog.comment.CommentUpdateRequest;
import com.backend.dto.response.blog.comment.CommentResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Blog;
import com.backend.entity.Category;
import com.backend.entity.CategoryBlog;
import com.backend.entity.Comment;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.CategoryMapper;
import com.backend.mapper.CommentMapper;
import com.backend.repository.BlogRepository;
import com.backend.repository.CommentRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.product.CategoryRepository;
import com.backend.repository.user.UserRepository;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
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

	CommentMapper commentMapper;

	public CommentResponse createComment(CommentCreationRequest request) {

		System.out.println("dữ liệu lấy được  từ request ; "+request);
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

	public CommentResponse updateComment(CommentUpdateRequest request, int commentId) {

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

		comment.setContent(request.getContent());

		return commentMapper.toCommentResponse(commentRepository.save(comment));
	}

	public void deleteComment(int commentId) {
		commentRepository.deleteById(commentId);
	}

	public PagedResponse<Comment> getComment(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Comment> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Comment> query = customSearchService.buildSearchQuery(Comment.class, search, sort);

		List<Comment> comments = entityManager.createQuery(query).setFirstResult((page - 1) * limit)
				.setMaxResults(limit).getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Comment.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(comments, page, totalPages, totalElements, limit);
	}

	public List<Comment> getCommentsByBlogId(Integer blogId) {
		return commentRepository.findByBlog_BlogId(blogId);
	}

}
