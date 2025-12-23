package com.example.itsme.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.itsme.domain.User;
import com.example.itsme.dto.AuthResponse;
import com.example.itsme.dto.LoginRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication endpoints using email as login id")
@RequiredArgsConstructor
public class AuthController {

	private final UserRepository userRepository;

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login with email/password", description = "Matches email as the login identifier and compares plain-text password.")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + request.email()));
		if (!user.getPassword().equals(request.password())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		return new AuthResponse(user.getUserId(), user.getEmail(), user.getName());
	}
}
