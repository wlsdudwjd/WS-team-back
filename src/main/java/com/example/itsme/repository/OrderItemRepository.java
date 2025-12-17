package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	List<OrderItem> findByOrderOrderId(Long orderId);
}
