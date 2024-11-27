package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Comment;
@Repository 
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByBlog_BlogId(Integer blogId);

}
