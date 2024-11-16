package com.backend.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
