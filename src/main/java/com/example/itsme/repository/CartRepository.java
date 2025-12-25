package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Page<Cart> findByUserUserId(Long userId, Pageable pageable);
}
