package com.example.itsme.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartCartIdAndMenuMenuId(Long cartId, Long menuId);

	Page<CartItem> findByCartCartId(Long cartId, Pageable pageable);
}
