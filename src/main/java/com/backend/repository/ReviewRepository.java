package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.backend.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> , JpaSpecificationExecutor<Review>{
	List<Review> findByProductId(Long productId);
}
