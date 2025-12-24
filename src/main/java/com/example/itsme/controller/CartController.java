package com.example.itsme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Cart;
import com.example.itsme.domain.User;
import com.example.itsme.dto.CartRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.CartRepository;
import com.example.itsme.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Carts", description = "Manage shopping carts per user")
public class CartController {

	private final CartRepository cartRepository;
	private final UserRepository userRepository;

	@GetMapping
	@Operation(summary = "장바구니 목록 조회", description = "사용자 ID/이메일로 해당 사용자의 장바구니 목록을 조회합니다.")
	public List<Cart> getCarts(@RequestParam(required = false) Long userId,
			@RequestParam(required = false) String userEmail) {
		User user = resolveUser(userId, userEmail);
		return cartRepository.findByUserUserId(user.getUserId());
	}

	@GetMapping("/{id}")
	@Operation(summary = "장바구니 단건 조회", description = "cartId로 장바구니를 조회합니다.")
	public Cart getCart(@PathVariable Long id) {
		return fetchCart(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "장바구니 생성", description = "사용자에게 빈 장바구니를 생성합니다.")
	public Cart createCart(@Valid @RequestBody CartRequest request) {
		User user = resolveUser(request.userId(), request.userEmail());
		Cart cart = Cart.builder()
				.user(user)
				.build();
		return cartRepository.save(cart);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "장바구니 삭제", description = "cartId로 장바구니를 삭제합니다.")
	public void deleteCart(@PathVariable Long id) {
		Cart cart = fetchCart(id);
		cartRepository.delete(cart);
	}

	private Cart fetchCart(Long id) {
		return cartRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + id));
	}

	private User resolveUser(Long userId, String userEmail) {
		if (userId != null) {
			return userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		}
		if (userEmail != null && !userEmail.isBlank()) {
			return userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
		}
		throw new ResourceNotFoundException("User identifier required");
	}
}
