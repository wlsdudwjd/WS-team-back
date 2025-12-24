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
import com.example.itsme.dto.UserRequest;
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
		return new AuthResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getName());
	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Signup with email/password", description = "Creates a user with the provided email, password, name, phone. Email must be unique.")
	public AuthResponse signup(@Valid @RequestBody UserRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists: " + request.email());
		}

		User user = User.builder()
				.email(request.email())
				.username(request.username())
				.password(request.password())
				.name(request.name())
				.phone(request.phone())
				.build();

		User saved = userRepository.save(user);
		return new AuthResponse(saved.getUserId(), saved.getUsername(), saved.getEmail(), saved.getName());
	}
}
