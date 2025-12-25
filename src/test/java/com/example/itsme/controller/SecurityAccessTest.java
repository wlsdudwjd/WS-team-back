package com.example.itsme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(com.example.itsme.config.TestFirebaseConfig.class)
class SecurityAccessTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void unauthorizedRequest_toApi_returns401() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void invalidToken_returns401() throws Exception {
		mockMvc.perform(get("/api/users")
				.header("Authorization", "Bearer invalid-token"))
				.andExpect(status().is4xxClientError());
	}
}
