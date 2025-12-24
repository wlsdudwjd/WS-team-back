package com.example.itsme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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

import com.example.itsme.domain.Menu;
import com.example.itsme.domain.Order;
import com.example.itsme.domain.OrderItem;
import com.example.itsme.domain.Store;
import com.example.itsme.domain.User;
import com.example.itsme.dto.OrderItemRequest;
import com.example.itsme.dto.OrderRequest;
import com.example.itsme.dto.OrderStatusUpdateRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.MenuRepository;
import com.example.itsme.repository.OrderItemRepository;
import com.example.itsme.repository.OrderRepository;
import com.example.itsme.repository.StoreRepository;
import com.example.itsme.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;

	@GetMapping
	public List<Order> getOrders(@RequestParam(required = false) Long userId,
			@RequestParam(required = false) String userEmail,
			@RequestParam(required = false) Long storeId) {
		User user = resolveUser(userId, userEmail);
		if (storeId != null) {
			return orderRepository.findByUserUserIdAndStoreStoreId(user.getUserId(), storeId);
		}
		return orderRepository.findByUserUserId(user.getUserId());
	}

	@GetMapping("/{id}")
	public Order getOrder(@PathVariable Long id) {
		return fetchOrder(id);
	}

	@PostMapping
	@Transactional
	@ResponseStatus(HttpStatus.CREATED)
	public Order createOrder(@Valid @RequestBody OrderRequest request) {
		Order order = Order.builder()
				.user(resolveUser(request.userId(), request.userEmail()))
				.store(fetchStore(request.storeId()))
				.status(request.status())
				.totalPrice(request.totalPrice())
				.build();
		Order savedOrder = orderRepository.save(order);
		request.items().forEach(itemRequest -> orderItemRepository.save(buildOrderItem(savedOrder, itemRequest)));
		return savedOrder;
	}

	@PutMapping("/{id}/status")
	public Order updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
		Order order = fetchOrder(id);
		order.setStatus(request.status());
		return orderRepository.save(order);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOrder(@PathVariable Long id) {
		Order order = fetchOrder(id);
		orderRepository.delete(order);
	}

	private Order fetchOrder(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
	}

	private User resolveUser(Long id, String email) {
		if (id != null) {
			return userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
		}
		if (email != null && !email.isBlank()) {
			return userRepository.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		}
		throw new ResourceNotFoundException("User identifier is required");
	}

	private Store fetchStore(Long id) {
		return storeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found: " + id));
	}

	private Menu fetchMenu(Long id) {
		return menuRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + id));
	}

	private OrderItem buildOrderItem(Order order, OrderItemRequest request) {
		Menu menu = fetchMenu(request.menuId());
		return OrderItem.builder()
				.order(order)
				.menu(menu)
				.quantity(request.quantity())
				.unitPrice(request.unitPrice())
				.build();
	}
}
