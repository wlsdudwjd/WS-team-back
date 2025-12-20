package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByUserUserId(Long userId);

	List<Payment> findByOrderOrderId(Long orderId);
}
