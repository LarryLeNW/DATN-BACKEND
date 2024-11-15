package com.backend.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
