package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
