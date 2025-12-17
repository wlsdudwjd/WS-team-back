package com.example.itsme.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartCartIdAndMenuMenuId(Long cartId, Long menuId);

	List<CartItem> findByCartCartId(Long cartId);
}
