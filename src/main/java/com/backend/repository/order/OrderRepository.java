package com.backend.repository.order;

import com.backend.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
