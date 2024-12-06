package com.backend.repository.fqa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.backend.entity.Blog;
import com.backend.entity.Product;
import com.backend.entity.Question;
import com.backend.entity.QuestionReaction;
import com.backend.entity.QuestionReply;

@Repository
public interface QuestionReactionRepository
        extends JpaRepository<QuestionReaction, Long>, JpaSpecificationExecutor<QuestionReaction> {
}
