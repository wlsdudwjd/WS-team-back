package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
