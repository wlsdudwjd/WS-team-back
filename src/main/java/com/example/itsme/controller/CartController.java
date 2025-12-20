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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Validated
public class CartController {

	private final CartRepository cartRepository;
	private final UserRepository userRepository;

	@GetMapping
	public List<Cart> getCarts(@RequestParam(required = false) Long userId) {
		if (userId == null) {
			return cartRepository.findAll();
		}
		return cartRepository.findByUserUserId(userId);
	}

	@GetMapping("/{id}")
	public Cart getCart(@PathVariable Long id) {
		return fetchCart(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Cart createCart(@Valid @RequestBody CartRequest request) {
		User user = fetchUser(request.userId());
		Cart cart = Cart.builder()
				.user(user)
				.build();
		return cartRepository.save(cart);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCart(@PathVariable Long id) {
		Cart cart = fetchCart(id);
		cartRepository.delete(cart);
	}

	private Cart fetchCart(Long id) {
		return cartRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + id));
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}
}
