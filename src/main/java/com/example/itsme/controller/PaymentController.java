package com.example.itsme.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Order;
import com.example.itsme.domain.Payment;
import com.example.itsme.domain.User;
import com.example.itsme.dto.PaymentRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.OrderRepository;
import com.example.itsme.repository.PaymentRepository;
import com.example.itsme.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Payments", description = "Payments for orders and users")
public class PaymentController {

	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "결제 목록 조회", description = "사용자/주문 기준 결제 목록을 페이지네이션 조회")
	public Page<Payment> getPayments(@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Long orderId,
			Pageable pageable) {
		if (userId != null) {
			return paymentRepository.findByUserUserId(userId, pageable);
		}
		if (orderId != null) {
			return paymentRepository.findByOrderOrderId(orderId, pageable);
		}
		return paymentRepository.findAll(pageable);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "결제 단건 조회", description = "paymentId로 결제를 조회합니다")
	public Payment getPayment(@PathVariable Long id) {
		return fetchPayment(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "결제 생성", description = "사용자/주문/금액 정보를 받아 결제를 생성합니다")
	public Payment createPayment(@Valid @RequestBody PaymentRequest request) {
		Payment payment = Payment.builder()
				.user(fetchUser(request.userId()))
				.order(fetchOrder(request.orderId()))
				.method(request.method())
				.amount(request.amount())
				.build();
		return paymentRepository.save(payment);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "결제 수정", description = "paymentId로 결제 정보를 수정합니다")
	public Payment updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
		Payment payment = fetchPayment(id);
		payment.setUser(fetchUser(request.userId()));
		payment.setOrder(fetchOrder(request.orderId()));
		payment.setMethod(request.method());
		payment.setAmount(request.amount());
		return paymentRepository.save(payment);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "결제 삭제", description = "paymentId로 결제를 삭제합니다 (관리자 전용)")
	public void deletePayment(@PathVariable Long id) {
		Payment payment = fetchPayment(id);
		paymentRepository.delete(payment);
	}

	private Payment fetchPayment(Long id) {
		return paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}

	private Order fetchOrder(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
	}
}
