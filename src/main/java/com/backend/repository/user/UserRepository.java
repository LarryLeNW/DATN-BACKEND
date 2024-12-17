package com.backend.repository.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.constant.Type.UserStatusType;
import com.backend.dto.response.user.TopOrderUser;
import com.backend.entity.Product;
import com.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
	boolean existsByUsername(String username);

	User findByEmail(String email);

	Optional<User> findByEmailAndStatusNot(String email, UserStatusType status);

	Optional<User> findByUsername(String username);

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	@Query("SELECT u.id, u.username, u.avatar ,  SUM(p.amount) AS totalAmount, COUNT(p) AS paymentCount "
			+ "FROM User u JOIN Payment p ON u.id = p.user.id " + "WHERE p.status = 'COMPLETED' "
			+ "GROUP BY u.id, u.username , u.avatar " + "ORDER BY totalAmount DESC")
	List<Object[]> findTop10UsersByPaymentAmount();

	@Query("SELECT DAY(o.createdAt), COUNT(o) " + "FROM User o "
			+ "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " + "GROUP BY DAY(o.createdAt) "
			+ "ORDER BY DAY(o.createdAt)")
	List<Object[]> countOrdersByDayInMonth(@Param("month") int month, @Param("year") int year);

}
