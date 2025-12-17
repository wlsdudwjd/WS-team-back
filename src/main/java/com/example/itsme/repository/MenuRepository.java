package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
