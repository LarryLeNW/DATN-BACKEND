package com.backend.repository.order;

import java.util.List;
import java.util.Optional;

import com.backend.entity.Product;
import com.backend.entity.Sku;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;


@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
	OrderDetail findOneByOrderAndProductAndSku(Order order, Product product, Sku sku);
	List<OrderDetail> findByOrder(Order order);
}
