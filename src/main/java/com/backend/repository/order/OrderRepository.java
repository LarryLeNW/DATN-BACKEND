package com.backend.repository.order;

import com.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	Order findOneByOrderCode(String orderCode);
}
