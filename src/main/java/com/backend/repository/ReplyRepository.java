package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.dto.response.blog.reply.ReplyResponse;
import com.backend.entity.Reply;
@Repository
public interface ReplyRepository extends JpaRepository<Reply,Integer> {
    List<Reply> findByComment_CommentId(Integer commentId);
    List<Reply> findByParentReply_ReplyId(Integer parentReplyId);

}
