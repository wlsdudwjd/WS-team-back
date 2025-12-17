package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
}
