package com.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.blog.BlogCreationRequest;
import com.backend.dto.request.blog.BlogUpdateRequest;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Blog;
import com.backend.entity.Brand;
import com.backend.entity.CategoryBlog;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BlogMapper;
import com.backend.repository.BlogRepository;
import com.backend.repository.CategoryBlogRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.user.UserRepository;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogService {

	BlogRepository blogRepository;

	CategoryBlogRepository categoryBlogRepository;

	UserRepository userRepository;

	EntityManager entityManager;

	BlogMapper blogMapper;

	@Autowired
	private UploadFile uploadFile;

	public BlogResponse createBlog(BlogCreationRequest request, List<MultipartFile> images) {
		Blog blog = new Blog();

		if (request.getCategoryBlogId() != 0) {
			CategoryBlog categoryBlog = categoryBlogRepository.findById(request.getCategoryBlogId())
					.orElseThrow(() -> new AppException(ErrorCode.CATEGORYBLOG_NOT_EXISTED));
			blog.setCategoryBlog(categoryBlog);
		}

		if (request.getUserId() != null) {
			User user = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			blog.setUser(user);
		}
		blog.setTitle(request.getTitle());
		blog.setContent(request.getContent());

		StringBuilder imagesString = new StringBuilder();
		for (MultipartFile image : images) {
			String imageUrl = uploadFile.saveFile(image, "blogTest");
			if (imagesString.length() > 0) {
				imagesString.append(",");
			}
			imagesString.append(imageUrl);
		}

		blog.setImages(imagesString.toString());
		
		return blogMapper.toBlogResponse(blogRepository.save(blog));

	}
	public BlogResponse getBlogById(Integer blogId) {
	    Blog blog = blogRepository.findById(blogId)
	            .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_EXISTED));

	    return blogMapper.toBlogResponse(blog);
	}

	public BlogResponse updateBlog(BlogUpdateRequest request,List<MultipartFile> images, Integer blogId) {
		Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_EXISTED));

		if (request.getCategoryBlogId() != 0) {
			CategoryBlog categoryBlog = categoryBlogRepository.findById(request.getCategoryBlogId())
					.orElseThrow(() -> new AppException(ErrorCode.CATEGORYBLOG_NOT_EXISTED));
			blog.setCategoryBlog(categoryBlog);
		}

		if (request.getUserId() != null) {
			User user = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			blog.setUser(user);
		}

		if (request != null) {
			Helpers.updateFieldEntityIfChanged(request.getTitle(), blog.getTitle(), blog::setTitle);
			Helpers.updateFieldEntityIfChanged(request.getContent(), blog.getContent(), blog::setContent);

			StringBuilder imagesString = new StringBuilder();
			for (MultipartFile image : images) {
				String imageUrl = uploadFile.saveFile(image, "blogTest");
				if (imagesString.length() > 0) {
					imagesString.append(",");
				}
				imagesString.append(imageUrl);
			}

			blog.setImages(imagesString.toString());
		}
		return blogMapper.toBlogResponse(blogRepository.save(blog));
	}

	public void deleteBlog(Integer blogId) {
		blogRepository.deleteById(blogId);
	}


    public PagedResponse<BlogResponse> getBlogs(int page, int limit, String sort, String... search) {
        List<SearchType> criteriaList = new ArrayList<>();
        CustomSearchRepository<Blog> customSearchService = new CustomSearchRepository<>(entityManager);

        CriteriaQuery<Blog> query = customSearchService.buildSearchQuery(Blog.class, search, sort);

        List<Blog> blogs = entityManager.createQuery(query)
            .setFirstResult((page - 1) * limit)
            .setMaxResults(limit)
            .getResultList();

        List<BlogResponse> blogResponses = blogs.stream()
            .map(blogMapper::toBlogResponse)
            .toList();

        CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Blog.class, search);
        long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        int totalPages = (int) Math.ceil((double) totalElements / limit);

        return new PagedResponse<>(blogResponses, page, totalPages, totalElements, limit);
    }

}
