package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.MenuLike;
import com.example.itsme.domain.MenuLikeId;

public interface MenuLikeRepository extends JpaRepository<MenuLike, MenuLikeId> {
}
