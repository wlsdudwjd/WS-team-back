package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
