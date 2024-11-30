package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
