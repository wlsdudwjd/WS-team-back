package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.MenuCategory;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
	Page<MenuCategory> findByServiceTypeServiceTypeId(Long serviceTypeId, Pageable pageable);
}
