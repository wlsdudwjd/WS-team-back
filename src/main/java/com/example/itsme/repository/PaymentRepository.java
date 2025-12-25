package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Page<Payment> findByUserUserId(Long userId, Pageable pageable);

	Page<Payment> findByOrderOrderId(Long orderId, Pageable pageable);
}
