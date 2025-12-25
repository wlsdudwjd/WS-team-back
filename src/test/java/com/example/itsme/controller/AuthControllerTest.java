package com.example.itsme.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.itsme.config.TestFirebaseConfig;
import com.example.itsme.domain.Role;
import com.example.itsme.domain.User;
import com.example.itsme.repository.UserRepository;
import com.example.itsme.service.PasswordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestFirebaseConfig.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		userRepository.deleteAll();
		User user = User.builder()
				.email("user@example.com")
				.username("user1")
				.password(passwordService.hash("password"))
				.name("User One")
				.role(Role.USER)
				.build();
		userRepository.save(user);
	}

	@Test
	void login_returnsTokens() throws Exception {
		String body = """
				{
				  "email": "user@example.com",
				  "password": "password"
				}
				""";

		String response = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode json = objectMapper.readTree(response);
		assertThat(json.get("accessToken").asText()).isNotBlank();
		assertThat(json.get("refreshToken").asText()).isNotBlank();
	}

	@Test
	void refresh_invalidToken_returnsUnauthorized() throws Exception {
		String body = """
				{
				  "refreshToken": "invalid.token"
				}
				""";

		mockMvc.perform(post("/api/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
				.andExpect(status().isUnauthorized());
	}
}
