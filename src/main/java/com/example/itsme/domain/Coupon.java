package com.example.itsme.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id")
	private Long couponId;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "discount_type", length = 50)
	private DiscountType discountType;

	@NotNull
	@Column(name = "discount_value", nullable = false)
	private Integer discountValue;

	@Column(name = "valid_from")
	private LocalDateTime validFrom;

	@Future
	@Column(name = "valid_to")
	private LocalDateTime validTo;
}
