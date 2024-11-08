package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
