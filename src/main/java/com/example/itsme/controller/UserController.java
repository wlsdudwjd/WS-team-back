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

import com.example.itsme.domain.User;
import com.example.itsme.dto.UserRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

	private final UserRepository userRepository;

	@GetMapping
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable Long id) {
		return fetchUser(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User createUser(@Valid @RequestBody UserRequest request) {
		User user = User.builder()
				.password(request.password())
				.email(request.email())
				.name(request.name())
				.phone(request.phone())
				.build();
		return userRepository.save(user);
	}

	@PutMapping("/{id}")
	public User updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
		User user = fetchUser(id);
		user.setPassword(request.password());
		user.setEmail(request.email());
		user.setName(request.name());
		user.setPhone(request.phone());
		return userRepository.save(user);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long id) {
		User user = fetchUser(id);
		userRepository.delete(user);
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}
}
