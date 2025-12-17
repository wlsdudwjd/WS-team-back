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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Menu;
import com.example.itsme.domain.Order;
import com.example.itsme.domain.OrderItem;
import com.example.itsme.dto.OrderItemCreateRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.MenuRepository;
import com.example.itsme.repository.OrderItemRepository;
import com.example.itsme.repository.OrderRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@Validated
public class OrderItemController {

	private final OrderItemRepository orderItemRepository;
	private final OrderRepository orderRepository;
	private final MenuRepository menuRepository;

	@GetMapping("/{id}")
	public OrderItem getOrderItem(@PathVariable Long id) {
		return fetchOrderItem(id);
	}

	@GetMapping("/order/{orderId}")
	public List<OrderItem> getItemsByOrder(@PathVariable Long orderId) {
		fetchOrder(orderId);
		return orderItemRepository.findByOrderOrderId(orderId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
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
}
