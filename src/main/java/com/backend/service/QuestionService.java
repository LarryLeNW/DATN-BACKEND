package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.backend.constant.Type.QuestionStatusType;
import com.backend.constant.Type.UserStatusType;
import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;
import com.backend.dto.request.fqa.QuestionCreation;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.question.QuestionReplyResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.Category;
import com.backend.entity.Question;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.CategoryMapper;
import com.backend.mapper.QuestionMapper;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.fqa.QuestionRepository;
import com.backend.repository.product.CategoryRepository;
import com.backend.repository.user.UserRepository;
import com.backend.specification.CartSpecification;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionService {
	QuestionRepository questionRepository;

	QuestionMapper questionMapper;

	UserRepository userRepository;
	
	EntityManager entityManager; 

	public QuestionResponse create(QuestionCreation request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User userAction = userRepository.findById(idUser)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		Question question = questionMapper.toQuestion(request);

		question.setUser(userAction);
		
		return questionMapper.toQuestionResponse(questionRepository.save(question)) ;
	}


	public PagedResponse<QuestionResponse> getAll(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);
		
		Specification<Question> spec = Specification.where(null);
		if (params.containsKey("status")) {
			String status = params.get("status");
			spec = spec
					.and((root, query, builder) -> builder.equal(root.get("status"), UserStatusType.valueOf(status)));
		}
		
		
		Page<Question> questionPage = questionRepository.findAll(spec, pageable);

	    List<QuestionResponse> questionResponses = questionPage.getContent().stream()
	            .map(question -> {
	                List<QuestionReplyResponse> filteredReplies = question.getReplies().stream()
	                    .filter(reply -> reply.getParentReply() == null)  
	                    .map(questionMapper::toQuestionReplyResponse)
	                    .collect(Collectors.toList());
	                QuestionResponse questionResponse = questionMapper.toQuestionResponse(question);
	                questionResponse.setReplies(filteredReplies); 
	                return questionResponse;
	            })
	            .collect(Collectors.toList());

		return new PagedResponse<>(questionResponses, page + 1, questionPage.getTotalPages(), questionPage.getTotalElements(),
				limit);
	}

}