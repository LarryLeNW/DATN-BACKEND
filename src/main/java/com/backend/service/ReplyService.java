package com.backend.service;

import java.util.ArrayList;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.dto.request.reply.ReplyCreationRequest;
import com.backend.dto.request.reply.ReplyUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.reply.ReplyResponse;
import com.backend.entity.Blog;
import com.backend.entity.Comment;
import com.backend.entity.Reply;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ReplyMapper;
import com.backend.repository.CommentRepository;
import com.backend.repository.ReplyRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.user.UserRepository;

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
public class ReplyService {
	EntityManager entityManager;

	ReplyMapper replyMapper;

	ReplyRepository replyRepository;

	UserRepository userRepository;

	CommentRepository commentRepository;

	public ReplyResponse createReply(ReplyCreationRequest request) {

		Reply reply = new Reply();

		if (request.getUserId() != null) {
			User user = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			reply.setUser(user);
		}

		if (request.getCommentId() != 0) {
			Comment comment = commentRepository.findById(request.getCommentId())
					.orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
			reply.setComment(comment);
		}

		reply.setContent(reply.getContent());
		return replyMapper.toReplyResponse(replyRepository.save(reply));

	}
	public ReplyResponse updateReply(ReplyUpdateRequest request, int replyId) {
		
		Reply reply = replyRepository.findById(replyId).orElseThrow(()-> new AppException(ErrorCode.REVIEW_NOT_FOUND));
		
		reply.setContent(request.getContent());
		
		return replyMapper.toReplyResponse(replyRepository.save(reply));
	}
	
	public void deleteReply (int replyId) {
		replyRepository.deleteById(replyId);
	}
	public PagedResponse<Reply> getReplys(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Reply> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Reply> query = customSearchService.buildSearchQuery(Reply.class, search, sort);

		List<Reply> reply = entityManager.createQuery(query).setFirstResult((page - 1) * limit).setMaxResults(limit)
				.getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Reply.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(reply, page, totalPages, totalElements, limit);
	}

}