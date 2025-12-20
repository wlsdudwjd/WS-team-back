package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserUserId(Long userId);

	List<Order> findByStoreStoreId(Long storeId);

	List<Order> findByUserUserIdAndStoreStoreId(Long userId, Long storeId);
}
