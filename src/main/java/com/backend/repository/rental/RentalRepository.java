package com.backend.repository.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.entity.Order;
import com.backend.entity.Payment;
import com.backend.entity.rental.Rental;

import java.time.LocalDateTime;
import java.util.List;
import com.backend.entity.User;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
	Rental findByPayment(Payment payment);

	Rental findByIdAndUser(Long id, User user);

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	long count();

	@Query("SELECT DAY(r.createdAt), COUNT(r) " + "FROM Rental r "
			+ "WHERE MONTH(r.createdAt) = :month AND YEAR(r.createdAt) = :year " + "GROUP BY DAY(r.createdAt) "
			+ "ORDER BY DAY(r.createdAt)")
	List<Object[]> countRentalsByDayInMonth(@Param("month") int month, @Param("year") int year);

}
