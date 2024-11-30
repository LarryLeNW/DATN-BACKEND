package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Blog;
@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

}
