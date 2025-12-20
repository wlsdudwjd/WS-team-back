package com.example.itsme.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "user_coupon",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_user_coupon_user_coupon", columnNames = { "user_id", "coupon_id" })
		},
		indexes = {
				@Index(name = "idx_user_coupon_user_id", columnList = "user_id"),
				@Index(name = "idx_user_coupon_coupon_id", columnList = "coupon_id")
		})
public class UserCoupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_coupon_id")
	private Long userCouponId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "coupon_id")
	private Coupon coupon;

	@Builder.Default
	@Column(name = "is_valid", nullable = false)
	private Boolean isValid = true;
}
