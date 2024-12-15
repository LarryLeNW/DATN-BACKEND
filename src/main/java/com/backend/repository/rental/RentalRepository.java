package com.backend.repository.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.backend.entity.Order;
import com.backend.entity.Payment;
import com.backend.entity.rental.Rental;
import java.util.List;
import com.backend.entity.User;


public interface RentalRepository extends JpaRepository<Rental, Long> ,JpaSpecificationExecutor<Rental> {
	Rental findByPayment(Payment payment);
	Rental findByIdAndUser(Long id, User user);
}
