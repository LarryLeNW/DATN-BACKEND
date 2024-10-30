package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Integer> {

}
