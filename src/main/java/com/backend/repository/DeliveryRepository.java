package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery,Integer> {

}
