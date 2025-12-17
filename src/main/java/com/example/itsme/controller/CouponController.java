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
import com.example.itsme.dto.CouponRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.CouponRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Validated
public class CouponController {

	private final CouponRepository couponRepository;

	@GetMapping
	public List<Coupon> getCoupons() {
		return couponRepository.findAll();
	}

	@GetMapping("/{id}")
	public Coupon getCoupon(@PathVariable Long id) {
		return fetchCoupon(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Coupon createCoupon(@Valid @RequestBody CouponRequest request) {
		Coupon coupon = Coupon.builder()
				.name(request.name())
				.discountType(request.discountType())
				.discountValue(request.discountValue())
				.validFrom(request.validFrom())
				.validTo(request.validTo())
				.build();
		return couponRepository.save(coupon);
	}

	@PutMapping("/{id}")
	public Coupon updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
		Coupon coupon = fetchCoupon(id);
		coupon.setName(request.name());
		coupon.setDiscountType(request.discountType());
		coupon.setDiscountValue(request.discountValue());
		coupon.setValidFrom(request.validFrom());
		coupon.setValidTo(request.validTo());
		return couponRepository.save(coupon);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCoupon(@PathVariable Long id) {
		Coupon coupon = fetchCoupon(id);
		couponRepository.delete(coupon);
	}

	private Coupon fetchCoupon(Long id) {
		return couponRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + id));
	}
}
