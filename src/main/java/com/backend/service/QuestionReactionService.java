package com.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.dto.request.fqa.QuestionReactionCreation;
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

	public QuestionReaction create(QuestionReactionCreation request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User userAction = userRepository.findById(idUser)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		Question questionFound = questionRepository.findById(request.getQuestionId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi nữa..."));

		QuestionReaction questionReaction = new QuestionReaction();
		questionReaction.setQuestion(questionFound);
		questionReaction.setUser(userAction);
		questionReaction.setReactionType(request.getReactionType());
		return questionReactionRepository.save(questionReaction);
	}

}