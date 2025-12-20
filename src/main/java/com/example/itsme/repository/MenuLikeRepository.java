package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.MenuLike;
import com.example.itsme.domain.MenuLikeId;

public interface MenuLikeRepository extends JpaRepository<MenuLike, MenuLikeId> {
	List<MenuLike> findByUserUserId(Long userId);

	List<MenuLike> findByMenuMenuId(Long menuId);
}
