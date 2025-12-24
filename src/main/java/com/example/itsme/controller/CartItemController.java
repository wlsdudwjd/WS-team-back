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
import com.example.itsme.repository.UserRepository;
import com.example.itsme.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
@Validated
@Tag(name = "Cart Items", description = "Manage items inside a cart")
public class CartItemController {

	private final CartItemRepository cartItemRepository;
	private final CartRepository cartRepository;
	private final MenuRepository menuRepository;
	private final UserRepository userRepository;

	@GetMapping("/cart/{cartId}")
	@Operation(summary = "장바구니별 아이템 조회", description = "cartId로 장바구니 안의 모든 상품을 조회합니다.")
	public List<CartItem> getItemsByCart(@PathVariable Long cartId) {
		fetchCart(cartId);
		return cartItemRepository.findByCartCartId(cartId);
	}

	@GetMapping("/{id}")
	@Operation(summary = "장바구니 아이템 조회", description = "cartItemId로 단일 아이템을 조회합니다.")
	public CartItem getCartItem(@PathVariable Long id) {
		return fetchCartItem(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "아이템 추가/수량 증가", description = "장바구니에 메뉴를 추가하거나 이미 있으면 수량을 증가시킵니다.")
	public CartItem createOrIncrement(@Valid @RequestBody CartItemRequest request) {
		Cart cart = fetchCart(request.cartId());
		if (request.userId() != null || (request.userEmail() != null && !request.userEmail().isBlank())) {
			User owner = resolveUser(request.userId(), request.userEmail());
			if (!cart.getUser().getUserId().equals(owner.getUserId())) {
				throw new ResourceNotFoundException("Cart does not belong to user: " + owner.getUserId());
			}
		}
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
	@Operation(summary = "아이템 수량 수정", description = "cartItemId로 장바구니 아이템의 수량을 변경합니다.")
	public CartItem updateQuantity(@PathVariable Long id,
			@Valid @RequestBody CartItemQuantityRequest request) {
		CartItem item = fetchCartItem(id);
		item.setQuantity(request.quantity());
		return cartItemRepository.save(item);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "아이템 삭제", description = "cartItemId로 장바구니 아이템을 삭제합니다.")
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
