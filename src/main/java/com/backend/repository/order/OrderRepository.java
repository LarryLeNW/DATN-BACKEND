package com.backend.repository.order;

import com.backend.entity.Order;
import com.backend.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.backend.entity.User;



@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> ,JpaSpecificationExecutor<Order>  {
	Order findOneByOrderCode(String orderCode);
	Order findByPayment(Payment payment);
	Order findByIdAndUser(Integer id, User user);
}
