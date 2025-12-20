package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	List<Store> findByServiceTypeServiceTypeId(Long serviceTypeId);
}
