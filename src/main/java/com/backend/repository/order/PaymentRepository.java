package com.backend.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Payment;
import java.util.List;
import com.backend.entity.User;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	Payment findByAppTransId(String app_trans_id);

	Payment findByAppTransIdAndUser(String appTransId, User user);
}
