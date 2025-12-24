package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
	List<UserCoupon> findByUserUserId(Long userId);

	List<UserCoupon> findByUserEmail(String email);
}
