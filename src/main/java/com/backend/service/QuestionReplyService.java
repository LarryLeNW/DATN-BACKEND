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
import com.backend.dto.request.fqa.QuestionReplyCreation;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.dto.response.user.UserResponse;
import com.backend.entity.Category;
import com.backend.entity.Question;
import com.backend.entity.QuestionReply;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.CategoryMapper;
import com.backend.mapper.QuestionMapper;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.fqa.QuestionReplyRepository;
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
public class QuestionReplyService {
	QuestionReplyRepository questionReplyRepository;

	QuestionRepository questionRepository;

	QuestionMapper questionMapper;

	UserRepository userRepository;

	EntityManager entityManager;

	public QuestionReply create(QuestionReplyCreation request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User userAction = userRepository.findById(idUser)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		Question questionFound = questionRepository.findById(request.getQuestionId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi nữa..."));

		QuestionReply questionReply = new QuestionReply();
		questionReply.setUser(userAction);
		questionReply.setReplyText(request.getReplyText());
		questionReply.setImages(request.getImages());
		questionReply.setQuestion(questionFound);

		if (request.getParentId() != null) {
			QuestionReply replyParent = questionReplyRepository.findById(request.getParentId())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận nữa..."));
			questionReply.setParentReply(replyParent);
		}

		return questionReplyRepository.save(questionReply);
	}

	public PagedResponse<QuestionReply> getAll(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);

		Specification<QuestionReply> spec = Specification.where(null);

		Page<QuestionReply> replyPage = questionReplyRepository.findAll(spec, pageable);

		List<QuestionReply> replyResponses = replyPage.getContent().stream().collect(Collectors.toList());

		return new PagedResponse<>(replyResponses, page + 1, replyPage.getTotalPages(), replyPage.getTotalElements(),
				limit);
	}

}