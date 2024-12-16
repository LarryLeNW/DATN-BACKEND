package com.backend.repository.order;

import com.backend.entity.Order;
import com.backend.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import com.backend.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
	Order findOneByOrderCode(String orderCode);

	Order findByPayment(Payment payment);

	Order findByIdAndUser(Integer id, User user);

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	long count();

	@Query("SELECT DAY(o.createdAt), COUNT(o) " + "FROM Order o "
			+ "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " + "GROUP BY DAY(o.createdAt) "
			+ "ORDER BY DAY(o.createdAt)")
	List<Object[]> countOrdersByDayInMonth(@Param("month") int month, @Param("year") int year);

}
