package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply,Integer> {

}
