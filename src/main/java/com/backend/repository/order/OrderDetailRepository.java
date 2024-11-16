package com.backend.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

}
