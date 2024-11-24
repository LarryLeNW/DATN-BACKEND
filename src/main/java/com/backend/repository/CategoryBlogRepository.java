package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.CategoryBlog;
@Repository
public interface CategoryBlogRepository extends JpaRepository<CategoryBlog, Integer> {

}
