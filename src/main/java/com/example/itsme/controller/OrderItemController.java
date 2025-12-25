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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Menu;
import com.example.itsme.domain.Order;
import com.example.itsme.domain.OrderItem;
import com.example.itsme.domain.User;
import com.example.itsme.dto.OrderItemCreateRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.MenuRepository;
import com.example.itsme.repository.OrderItemRepository;
import com.example.itsme.repository.OrderRepository;
import com.example.itsme.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@Validated
@Tag(name = "Order Items", description = "Items belonging to an order")
public class OrderItemController {

	private final OrderItemRepository orderItemRepository;
	private final OrderRepository orderRepository;
	private final MenuRepository menuRepository;
	private final UserRepository userRepository;

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "주문상품 단건 조회", description = "orderItemId로 주문상품을 조회(본인 소유 검증)")
	public OrderItem getOrderItem(@PathVariable Long id,
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) String userEmail) {
		OrderItem item = fetchOrderItem(id);
		User user = resolveUser(userId, userEmail);
		if (!item.getOrder().getUser().getUserId().equals(user.getUserId())) {
			throw new ResourceNotFoundException("Order item not found for user");
		}
		return item;
	}

	@GetMapping("/order/{orderId}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "주문별 상품 조회", description = "orderId로 주문상품 목록을 페이지네이션 조회(본인 소유 검증)")
	public Page<OrderItem> getItemsByOrder(@PathVariable Long orderId,
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) String userEmail,
			Pageable pageable) {
		Order order = fetchOrder(orderId);
		User user = resolveUser(userId, userEmail);
		if (!order.getUser().getUserId().equals(user.getUserId())) {
			throw new ResourceNotFoundException("Order not found for user");
		}
		return orderItemRepository.findByOrderOrderId(orderId, pageable);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "주문상품 생성", description = "주문/메뉴/수량으로 주문상품을 생성합니다")
	public OrderItem createOrderItem(@Valid @RequestBody OrderItemCreateRequest request) {
		Order order = fetchOrder(request.orderId());
		Menu menu = fetchMenu(request.menuId());
		OrderItem item = OrderItem.builder()
				.order(order)
				.menu(menu)
				.quantity(request.quantity())
				.unitPrice(request.unitPrice())
				.build();
		return orderItemRepository.save(item);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "주문상품 삭제", description = "orderItemId로 주문상품을 삭제합니다")
	public void deleteOrderItem(@PathVariable Long id) {
		OrderItem item = fetchOrderItem(id);
		orderItemRepository.delete(item);
	}

	private OrderItem fetchOrderItem(Long id) {
		return orderItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order item not found: " + id));
	}

	private Order fetchOrder(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
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
