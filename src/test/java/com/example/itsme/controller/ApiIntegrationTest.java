package com.example.itsme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.itsme.config.TestFirebaseConfig;
import com.example.itsme.domain.Menu;
import com.example.itsme.domain.MenuCategory;
import com.example.itsme.domain.Role;
import com.example.itsme.domain.ServiceType;
import com.example.itsme.domain.Store;
import com.example.itsme.domain.User;
import com.example.itsme.repository.CartItemRepository;
import com.example.itsme.repository.CartRepository;
import com.example.itsme.repository.MenuCategoryRepository;
import com.example.itsme.repository.MenuRepository;
import com.example.itsme.repository.NotificationRepository;
import com.example.itsme.repository.OrderItemRepository;
import com.example.itsme.repository.OrderRepository;
import com.example.itsme.repository.PaymentRepository;
import com.example.itsme.repository.ServiceTypeRepository;
import com.example.itsme.repository.StoreRepository;
import com.example.itsme.repository.UserRepository;
import com.example.itsme.security.JwtTokenProvider;
import com.example.itsme.service.PasswordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestFirebaseConfig.class)
class ApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ServiceTypeRepository serviceTypeRepository;

	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	private User admin;
	private User user;
	private String adminToken;
	private String userToken;
	private Menu baseMenu;

	@BeforeEach
	void init() {
		// clean
		orderItemRepository.deleteAll();
		orderRepository.deleteAll();
		paymentRepository.deleteAll();
		cartItemRepository.deleteAll();
		cartRepository.deleteAll();
		menuRepository.deleteAll();
		menuCategoryRepository.deleteAll();
		storeRepository.deleteAll();
		serviceTypeRepository.deleteAll();
		notificationRepository.deleteAll();
		userRepository.deleteAll();

		admin = userRepository.save(User.builder()
				.email("admin@example.com")
				.username("admin")
				.password(passwordService.hash("adminpass"))
				.role(Role.ADMIN)
				.name("Admin")
				.build());

		user = userRepository.save(User.builder()
				.email("user@example.com")
				.username("user")
				.password(passwordService.hash("userpass"))
				.role(Role.USER)
				.name("User")
				.build());

		adminToken = "Bearer " + jwtTokenProvider.generateAccessToken(admin);
		userToken = "Bearer " + jwtTokenProvider.generateAccessToken(user);

		ServiceType st = serviceTypeRepository.save(ServiceType.builder().name("Campus").build());
		MenuCategory cat = menuCategoryRepository
				.save(MenuCategory.builder().name("Coffee").serviceType(st).build());
		Store store = storeRepository.save(Store.builder().name("Main Cafe").serviceType(st).build());
		baseMenu = menuRepository.save(Menu.builder()
				.name("Americano")
				.price(3000)
				.description("Hot")
				.store(store)
				.category(cat)
				.build());
	}

	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

	@Test
	void admin_can_list_users() throws Exception {
		mockMvc.perform(get("/api/users").header("Authorization", adminToken))
				.andExpect(status().isOk());
	}

	@Test
	void user_cannot_list_users() throws Exception {
		int status = mockMvc.perform(get("/api/users").header("Authorization", userToken))
				.andReturn()
				.getResponse()
				.getStatus();
		System.out.println("user_cannot_list_users status=" + status);
		org.assertj.core.api.Assertions.assertThat(status).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

	@Test
	void menu_list_returns_ok() throws Exception {
		mockMvc.perform(get("/api/menus").header("Authorization", userToken))
				.andExpect(status().isOk());
	}

	@Test
	void admin_can_create_menu() throws Exception {
		Map<String, Object> body = Map.of(
				"name", "Latte",
				"price", 4000,
				"description", "Milk",
				"storeId", baseMenu.getStore().getStoreId(),
				"categoryId", baseMenu.getCategory().getMenuCategoryId());
		mockMvc.perform(post("/api/menus")
				.header("Authorization", adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated());
	}

	@Test
	void user_cannot_create_menu() throws Exception {
		Map<String, Object> body = Map.of(
				"name", "Latte",
				"price", 4000,
				"description", "Milk",
				"storeId", baseMenu.getStore().getStoreId(),
				"categoryId", baseMenu.getCategory().getMenuCategoryId());
		int status = mockMvc.perform(post("/api/menus")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andReturn()
				.getResponse()
				.getStatus();
		System.out.println("user_cannot_create_menu status=" + status);
		org.assertj.core.api.Assertions.assertThat(status).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

	@Test
	void admin_can_create_store() throws Exception {
		Map<String, Object> body = Map.of(
				"name", "Side Cafe",
				"serviceTypeId", baseMenu.getStore().getServiceType().getServiceTypeId());
		mockMvc.perform(post("/api/stores")
				.header("Authorization", adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated());
	}

	@Test
	void admin_can_create_category() throws Exception {
		Map<String, Object> body = Map.of(
				"name", "Tea",
				"serviceTypeId", baseMenu.getStore().getServiceType().getServiceTypeId());
		mockMvc.perform(post("/api/menu-categories")
				.header("Authorization", adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated());
	}

	@Test
	void admin_can_create_coupon() throws Exception {
		Map<String, Object> body = Map.of(
				"name", "WELCOME10",
				"discountType", "PERCENT",
				"discountValue", BigDecimal.TEN,
				"validFrom", "2025-01-01T00:00:00",
				"validTo", "2025-12-31T23:59:59");
		mockMvc.perform(post("/api/coupons")
				.header("Authorization", adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated());
	}

	@Test
	void user_cannot_create_coupon() throws Exception {
		Map<String, Object> body = Map.of(
				"name", "USERTRY",
				"discountType", "PERCENT",
				"discountValue", BigDecimal.TEN,
				"validFrom", "2025-01-01T00:00:00",
				"validTo", "2025-12-31T23:59:59");
		int status = mockMvc.perform(post("/api/coupons")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andReturn()
				.getResponse()
				.getStatus();
		System.out.println("user_cannot_create_coupon status=" + status);
		org.assertj.core.api.Assertions.assertThat(status).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

	@Test
	void user_can_create_cart_and_list() throws Exception {
		Map<String, Object> body = Map.of("userId", user.getUserId());
		mockMvc.perform(post("/api/carts")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/carts")
				.header("Authorization", userToken)
				.param("userId", user.getUserId().toString()))
				.andExpect(status().isOk());
	}

	@Test
	void user_can_create_order_and_list() throws Exception {
		Map<String, Object> orderBody = Map.of(
				"userId", user.getUserId(),
				"storeId", baseMenu.getStore().getStoreId(),
				"status", "PAYMENT_COMPLETE",
				"totalPrice", 3000,
				"items", List.of(Map.of(
						"menuId", baseMenu.getMenuId(),
						"quantity", 1,
						"unitPrice", 3000)));

		mockMvc.perform(post("/api/orders")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(orderBody)))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/orders")
				.header("Authorization", userToken)
				.param("userId", user.getUserId().toString()))
				.andExpect(status().isOk());
	}

	@Test
	void user_can_update_order_status() throws Exception {
		Long orderId = createOrder();
		Map<String, Object> body = Map.of("status", "PAYMENT_COMPLETE");
		mockMvc.perform(put("/api/orders/{id}/status", orderId)
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isOk());
	}

	@Test
	void user_can_create_payment() throws Exception {
		Long orderId = createOrder();
		Map<String, Object> body = Map.of(
				"userId", user.getUserId(),
				"orderId", orderId,
				"method", 1,
				"amount", 3000);
		mockMvc.perform(post("/api/payments")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated());
	}

	@Test
	void user_can_create_notification() throws Exception {
		Map<String, Object> body = Map.of(
				"userId", user.getUserId(),
				"title", "Hello",
				"body", "World");
		mockMvc.perform(post("/api/notifications")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated());
	}

	@Test
	void user_can_create_and_delete_cart_item() throws Exception {
		Long cartId = createCart();
		Map<String, Object> body = Map.of(
				"cartId", cartId,
				"menuId", baseMenu.getMenuId(),
				"quantity", 1);
		MvcResult result = mockMvc.perform(post("/api/cart-items")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated())
				.andReturn();
		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		long cartItemId = json.get("cartItemId").asLong();

		mockMvc.perform(delete("/api/cart-items/{id}", cartItemId)
				.header("Authorization", userToken))
				.andExpect(status().isNoContent());
	}

	@Test
	void user_can_list_order_items() throws Exception {
		Long orderId = createOrder();
		mockMvc.perform(get("/api/order-items/order/{orderId}", orderId)
				.header("Authorization", userToken)
				.param("userId", user.getUserId().toString()))
				.andExpect(status().isOk());
	}

	private Long createOrder() throws Exception {
		Map<String, Object> orderBody = Map.of(
				"userId", user.getUserId(),
				"storeId", baseMenu.getStore().getStoreId(),
				"status", "PAYMENT_COMPLETE",
				"totalPrice", 3000,
				"items", List.of(Map.of(
						"menuId", baseMenu.getMenuId(),
						"quantity", 1,
						"unitPrice", 3000)));

		MvcResult result = mockMvc.perform(post("/api/orders")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(orderBody)))
				.andExpect(status().is2xxSuccessful())
				.andReturn();
		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		return json.get("orderId").asLong();
	}

	private Long createCart() throws Exception {
		Map<String, Object> body = Map.of("userId", user.getUserId());
		MvcResult result = mockMvc.perform(post("/api/carts")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(body)))
				.andExpect(status().isCreated())
				.andReturn();
		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		return json.get("cartId").asLong();
	}
}
