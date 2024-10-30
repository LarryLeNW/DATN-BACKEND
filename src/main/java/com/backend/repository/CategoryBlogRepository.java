package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.CategoryBlog;

public interface CategoryBlogRepository extends JpaRepository<CategoryBlog, Integer> {

}
