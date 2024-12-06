package com.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.dto.request.fqa.QuestionReactionCreation;
import com.backend.dto.response.question.QuestionReactionResponse;
import com.backend.entity.Question;
import com.backend.entity.QuestionReaction;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.QuestionMapper;
import com.backend.repository.fqa.QuestionReactionRepository;
import com.backend.repository.fqa.QuestionRepository;
import com.backend.repository.user.UserRepository;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionReactionService {
	QuestionRepository questionRepository;

	QuestionMapper questionMapper;

	QuestionReactionRepository questionReactionRepository;

	UserRepository userRepository;

	EntityManager entityManager;

	public QuestionReactionResponse create(QuestionReactionCreation request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User userAction = userRepository.findById(idUser)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		Question questionFound = questionRepository.findById(request.getQuestionId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi nữa..."));

		QuestionReaction questionReactionCreated = questionReactionRepository
				.findByUserAndQuestionAndReactionType(userAction, questionFound, request.getReactionType());

		if (questionReactionCreated != null) {
			questionReactionCreated.setReactionType(request.getReactionType());
		} else {
			questionReactionCreated = new QuestionReaction();
			questionReactionCreated.setQuestion(questionFound);
			questionReactionCreated.setUser(userAction);
			questionReactionCreated.setReactionType(request.getReactionType());
		}

		questionReactionRepository.save(questionReactionCreated);
		return questionMapper.toQuestionReactionResponse(questionReactionCreated);
	}

	public QuestionReactionResponse update(Long id, QuestionReactionCreation request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User userAction = userRepository.findById(idUser)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		QuestionReaction questionReactionUpdated = questionReactionRepository.findByUserAndId(userAction, id);
		if(questionReactionUpdated == null) throw new RuntimeException("Vui lòng thử lại sau...");
		questionReactionUpdated.setReactionType(request.getReactionType());
		questionReactionRepository.save(questionReactionUpdated);
		return questionMapper.toQuestionReactionResponse(questionReactionUpdated);
	}

}