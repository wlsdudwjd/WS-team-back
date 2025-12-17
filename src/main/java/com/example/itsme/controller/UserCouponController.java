package com.example.itsme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Coupon;
import com.example.itsme.domain.User;
import com.example.itsme.domain.UserCoupon;
import com.example.itsme.dto.UserCouponRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.CouponRepository;
import com.example.itsme.repository.UserCouponRepository;
import com.example.itsme.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-coupons")
@RequiredArgsConstructor
@Validated
public class UserCouponController {

	private final UserCouponRepository userCouponRepository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;

	@GetMapping
	public List<UserCoupon> getUserCoupons() {
		return userCouponRepository.findAll();
	}

	@GetMapping("/{id}")
	public UserCoupon getUserCoupon(@PathVariable Long id) {
		return fetchUserCoupon(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserCoupon grantCoupon(@Valid @RequestBody UserCouponRequest request) {
		UserCoupon userCoupon = UserCoupon.builder()
				.user(fetchUser(request.userId()))
				.coupon(fetchCoupon(request.couponId()))
				.isValid(request.isValid())
				.build();
		return userCouponRepository.save(userCoupon);
	}

	@PutMapping("/{id}")
	public UserCoupon updateUserCoupon(@PathVariable Long id, @Valid @RequestBody UserCouponRequest request) {
		UserCoupon userCoupon = fetchUserCoupon(id);
		userCoupon.setUser(fetchUser(request.userId()));
		userCoupon.setCoupon(fetchCoupon(request.couponId()));
		userCoupon.setIsValid(request.isValid());
		return userCouponRepository.save(userCoupon);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUserCoupon(@PathVariable Long id) {
		UserCoupon userCoupon = fetchUserCoupon(id);
		userCouponRepository.delete(userCoupon);
	}

	private UserCoupon fetchUserCoupon(Long id) {
		return userCouponRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User coupon not found: " + id));
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}

	private Coupon fetchCoupon(Long id) {
		return couponRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + id));
	}
}
