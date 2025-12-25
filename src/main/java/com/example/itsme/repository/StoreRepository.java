package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	Page<Store> findByServiceTypeServiceTypeId(Long serviceTypeId, Pageable pageable);
}
