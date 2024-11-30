package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Reply;
@Repository
public interface ReplyRepository extends JpaRepository<Reply,Integer> {

}
