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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Validated
@Tag(name = "Coupons", description = "Create and manage coupons")
public class CouponController {

	private final CouponRepository couponRepository;

	@GetMapping
	@Operation(summary = "쿠폰 목록 조회", description = "발급 가능한 모든 쿠폰을 조회합니다.")
	public List<Coupon> getCoupons() {
		return couponRepository.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "쿠폰 단건 조회", description = "couponId로 쿠폰 상세를 조회합니다.")
	public Coupon getCoupon(@PathVariable Long id) {
		return fetchCoupon(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "쿠폰 생성", description = "할인 유형/값/유효기간을 포함한 쿠폰을 생성합니다.")
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
	@Operation(summary = "쿠폰 수정", description = "couponId로 쿠폰 정보를 수정합니다.")
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
	@Operation(summary = "쿠폰 삭제", description = "couponId로 쿠폰을 삭제합니다.")
	public void deleteCoupon(@PathVariable Long id) {
		Coupon coupon = fetchCoupon(id);
		couponRepository.delete(coupon);
	}

	private Coupon fetchCoupon(Long id) {
		return couponRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + id));
	}
}
