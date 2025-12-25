package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	Page<OrderItem> findByOrderOrderId(Long orderId, Pageable pageable);
}
