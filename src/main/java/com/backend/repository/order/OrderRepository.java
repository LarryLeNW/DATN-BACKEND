package com.backend.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.backend.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	
}
