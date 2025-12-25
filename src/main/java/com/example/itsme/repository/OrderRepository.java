package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Page<Order> findByUserUserId(Long userId, Pageable pageable);

	Page<Order> findByStoreStoreId(Long storeId, Pageable pageable);

	Page<Order> findByUserUserIdAndStoreStoreId(Long userId, Long storeId, Pageable pageable);
}
