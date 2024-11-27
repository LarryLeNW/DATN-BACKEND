package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.backend.entity.Delivery;
import com.backend.entity.User;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer>, JpaSpecificationExecutor<Delivery> {
	List<Delivery> findByUser(User user);

	void deleteByIdAndUser(int id, User user);

	Delivery findByIdAndUser(int id, User user);

	Delivery findFirstByUserAndIsDefaultTrue(User user);

	long countByUser(User user);
}
