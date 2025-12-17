package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
