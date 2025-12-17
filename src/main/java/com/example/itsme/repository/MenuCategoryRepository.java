package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.MenuCategory;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
}
