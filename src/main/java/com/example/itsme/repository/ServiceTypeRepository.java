package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.ServiceType;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
}
