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

import com.example.itsme.domain.Cart;
import com.example.itsme.domain.CartItem;
import com.example.itsme.domain.Menu;
import com.example.itsme.dto.CartItemQuantityRequest;
import com.example.itsme.dto.CartItemRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.CartItemRepository;
import com.example.itsme.repository.CartRepository;
import com.example.itsme.repository.MenuRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
@Validated
public class CartItemController {

	private final CartItemRepository cartItemRepository;
	private final CartRepository cartRepository;
	private final MenuRepository menuRepository;

	@GetMapping("/cart/{cartId}")
	public List<CartItem> getItemsByCart(@PathVariable Long cartId) {
		fetchCart(cartId);
		return cartItemRepository.findByCartCartId(cartId);
	}

	@GetMapping("/{id}")
	public CartItem getCartItem(@PathVariable Long id) {
		return fetchCartItem(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CartItem createOrIncrement(@Valid @RequestBody CartItemRequest request) {
		Cart cart = fetchCart(request.cartId());
		Menu menu = fetchMenu(request.menuId());
		return cartItemRepository.findByCartCartIdAndMenuMenuId(cart.getCartId(), menu.getMenuId())
				.map(existing -> {
					existing.setQuantity(existing.getQuantity() + request.quantity());
					return cartItemRepository.save(existing);
				})
				.orElseGet(() -> {
					CartItem item = CartItem.builder()
							.cart(cart)
							.menu(menu)
							.quantity(request.quantity())
							.build();
					return cartItemRepository.save(item);
				});
	}

	@PutMapping("/{id}")
	public CartItem updateQuantity(@PathVariable Long id,
			@Valid @RequestBody CartItemQuantityRequest request) {
		CartItem item = fetchCartItem(id);
		item.setQuantity(request.quantity());
		return cartItemRepository.save(item);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCartItem(@PathVariable Long id) {
		CartItem item = fetchCartItem(id);
		cartItemRepository.delete(item);
	}

	private CartItem fetchCartItem(Long id) {
		return cartItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + id));
	}

	private Cart fetchCart(Long id) {
		return cartRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + id));
	}

	private Menu fetchMenu(Long id) {
		return menuRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + id));
	}
}
