package com.backend.repository.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.backend.entity.Order;
import com.backend.entity.rental.Rental;
import com.backend.entity.rental.RentalDetail;

public interface RentalDetailRepository extends JpaRepository<RentalDetail, Long> ,JpaSpecificationExecutor<RentalDetail> {
}
