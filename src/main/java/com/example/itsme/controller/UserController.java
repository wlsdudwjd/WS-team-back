package com.example.itsme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "사용자목록 조회", description = "전체 사용자 리스트를 반환합니다")
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "사용자단건 조회", description = "userId로 사용자 상세 정보를 조회합니다")
	public User getUser(@PathVariable Long id) {
		return fetchUser(id);
	}

	@GetMapping(params = "email")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "이메일로 사용자조회", description = "이메일로 로그인 프로필을 조회합니다")
	public UserProfileResponse getUserByEmail(@RequestParam String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
		return UserProfileResponse.from(user);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "사용자생성(회원가입 아님)", description = "관리자용 사용자 등록 API")
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
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "사용자수정", description = "userId로 사용자를 수정합니다")
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
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "사용자삭제", description = "userId로 사용자를 삭제합니다")
	public void deleteUser(@PathVariable Long id) {
		User user = fetchUser(id);
		userRepository.delete(user);
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}
}
