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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.User;
import com.example.itsme.dto.UserProfileResponse;
import com.example.itsme.dto.UserRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "User CRUD. Email is the login identifier.")
public class UserController {

	private final UserRepository userRepository;

	@GetMapping
	@Operation(summary = "List users")
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get user by id")
	public User getUser(@PathVariable Long id) {
		return fetchUser(id);
	}

	@GetMapping(params = "email")
	@Operation(summary = "Get user by email", description = "Lookup user by email. Useful for showing profile info after login.")
	public UserProfileResponse getUserByEmail(@RequestParam String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
		return UserProfileResponse.from(user);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create user (signup)", description = "Registers a new user. Email must be unique and is used for login.")
	public User createUser(@Valid @RequestBody UserRequest request) {
		User user = User.builder()
				.username(request.username())
				.password(request.password())
				.email(request.email())
				.name(request.name())
				.phone(request.phone())
				.build();
		return userRepository.save(user);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update user")
	public User updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
		User user = fetchUser(id);
		user.setUsername(request.username());
		user.setPassword(request.password());
		user.setEmail(request.email());
		user.setName(request.name());
		user.setPhone(request.phone());
		return userRepository.save(user);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete user")
	public void deleteUser(@PathVariable Long id) {
		User user = fetchUser(id);
		userRepository.delete(user);
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}
}
