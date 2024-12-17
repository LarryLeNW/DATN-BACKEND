package com.backend.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import com.backend.entity.User;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	Payment findByAppTransId(String app_trans_id);

	Payment findByAppTransIdAndUser(String appTransId, User user);

	@Query("SELECT DAY(p.createdAt), COUNT(p), SUM(p.amount) " + "FROM Payment p "
			+ "WHERE MONTH(p.createdAt) = :month AND YEAR(p.createdAt) = :year AND p.status = 'COMPLETED' "
			+ "GROUP BY DAY(p.createdAt) " + "ORDER BY DAY(p.createdAt)")
	List<Object[]> countPaymentsAndRevenueByDayInMonth(@Param("month") int month, @Param("year") int year);

	@Query("SELECT COUNT(p) FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.status = 'COMPLETED'")
	long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


	@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.status = 'COMPLETED'")
	Long sumAmountByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
	Long sumAmount();
}
