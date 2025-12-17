package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
