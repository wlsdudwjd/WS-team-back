package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	List<Cart> findByUserUserId(Long userId);
}
