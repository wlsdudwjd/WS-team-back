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
import com.example.itsme.dto.FirebaseLoginRequest;
import com.example.itsme.dto.LoginRequest;
import com.example.itsme.dto.UserRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.UserRepository;
import com.example.itsme.service.FirebaseAuthService;
import com.example.itsme.service.FirebaseUser;
import com.example.itsme.service.PasswordService;

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
	private final FirebaseAuthService firebaseAuthService;
	private final PasswordService passwordService;

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login with email/password", description = "Matches email as the login identifier and compares plain-text password.")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + request.email()));
		if (passwordService.isHashed(user.getPassword())) {
			if (!passwordService.matches(request.password(), user.getPassword())) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
			}
		}
		else {
			if (!user.getPassword().equals(request.password())) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
			}
			user.setPassword(passwordService.hash(request.password()));
			userRepository.save(user);
		}
		if (user.getPassword() == null || user.getPassword().isBlank()) {
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
				.password(passwordService.hash(request.password()))
				.name(request.name())
				.phone(request.phone())
				.build();

		User saved = userRepository.save(user);
		return new AuthResponse(saved.getUserId(), saved.getUsername(), saved.getEmail(), saved.getName());
	}

	@PostMapping("/firebase-login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login with Firebase ID token", description = "Verifies Firebase ID token, syncs user in DB, and returns profile.")
	public AuthResponse firebaseLogin(@Valid @RequestBody FirebaseLoginRequest request) {
		FirebaseUser firebaseUser = firebaseAuthService.verify(request.idToken());
		if (firebaseUser.email() == null || firebaseUser.email().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firebase token missing email");
		}

		User user = userRepository.findByEmail(firebaseUser.email())
				.orElseGet(() -> createUserFromFirebase(firebaseUser, request));

		// 프로필이 비어 있을 때, 요청으로 받은 값으로 채워주기 (최소한 한 번은 채워지도록)
		boolean updated = false;
		if ((user.getName() == null || user.getName().isBlank())
				&& request.name() != null && !request.name().isBlank()) {
			user.setName(request.name());
			updated = true;
		}
		if ((user.getPhone() == null || user.getPhone().isBlank())
				&& request.phone() != null && !request.phone().isBlank()) {
			user.setPhone(request.phone());
			updated = true;
		}
		if (updated) {
			userRepository.save(user);
		}

		return new AuthResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getName());
	}

	private User createUserFromFirebase(FirebaseUser firebaseUser, FirebaseLoginRequest request) {
		String email = firebaseUser.email();
		String emailLocalPart = email != null && email.contains("@")
				? email.substring(0, email.indexOf("@"))
				: "firebase_" + firebaseUser.uid();

		// 요청으로 온 username/name/phone 우선 적용, 없으면 토큰/기본값
		String requestedUsername = request.username();
		String requestedName = request.name();
		String requestedPhone = request.phone();

		// username 우선순위: 요청 > 이메일 앞부분 > uid
		String usernameToUse = (requestedUsername != null && !requestedUsername.isBlank())
				? requestedUsername
				: emailLocalPart;

		// name 우선순위: 요청 > displayName > 이메일 앞부분
		String displayName = firebaseUser.displayName();
		String nameToUse = (requestedName != null && !requestedName.isBlank())
				? requestedName
				: (displayName != null && !displayName.isBlank() ? displayName : emailLocalPart);

		// phone 우선순위: 요청 > 토큰
		String phoneToUse = (requestedPhone != null && !requestedPhone.isBlank())
				? requestedPhone
				: firebaseUser.phoneNumber();

		User newUser = User.builder()
				.email(email)
				.username(usernameToUse)
				.password(passwordService.hash("firebase")) // Firebase manages credentials
				.name(nameToUse)
				.phone(phoneToUse)
				.build();
		return userRepository.save(newUser);
	}
}
