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
import com.example.itsme.dto.JwtAuthResponse;
import com.example.itsme.dto.FirebaseLoginRequest;
import com.example.itsme.dto.LoginRequest;
import com.example.itsme.dto.GoogleLoginRequest;
import com.example.itsme.dto.RefreshTokenRequest;
import com.example.itsme.dto.UserRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.UserRepository;
import com.example.itsme.service.FirebaseAuthService;
import com.example.itsme.service.FirebaseUser;
import com.example.itsme.service.GoogleTokenVerifier;
import com.example.itsme.service.GoogleTokenVerifier.GoogleUser;
import com.example.itsme.service.PasswordService;
import com.example.itsme.security.JwtTokenProvider;

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
	private final GoogleTokenVerifier googleTokenVerifier;
	private final PasswordService passwordService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login with email/password", description = "Matches email as the login identifier and compares plain-text password.")
	public JwtAuthResponse login(@Valid @RequestBody LoginRequest request) {
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
		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user);
		return JwtAuthResponse.of(user.getUserId(), user.getUsername(), user.getEmail(), user.getName(),
				user.getRole(), accessToken, refreshToken);
	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Signup with email/password", description = "Creates a user with the provided email, password, name, phone. Email must be unique.")
	public JwtAuthResponse signup(@Valid @RequestBody UserRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists: " + request.email());
		}

		var role = request.role() != null ? request.role() : com.example.itsme.domain.Role.USER;
		// 전화번호 유니크 제약 완화: 미입력/공백은 null로 저장하여 충돌 방지
		String normalizedPhone = (request.phone() == null || request.phone().isBlank()) ? null : request.phone();

		User user = User.builder()
				.email(request.email())
				.username(request.username())
				.password(passwordService.hash(request.password()))
				.name(request.name())
				.phone(normalizedPhone)
				.role(role)
				.build();

		User saved = userRepository.save(user);
		String accessToken = jwtTokenProvider.generateAccessToken(saved);
		String refreshToken = jwtTokenProvider.generateRefreshToken(saved);
		return JwtAuthResponse.of(saved.getUserId(), saved.getUsername(), saved.getEmail(), saved.getName(),
				saved.getRole(), accessToken, refreshToken);
	}

	@PostMapping("/firebase-login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login with Firebase ID token", description = "Verifies Firebase ID token, syncs user in DB, and returns profile.")
	public JwtAuthResponse firebaseLogin(@Valid @RequestBody FirebaseLoginRequest request) {
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

		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user);
		return JwtAuthResponse.of(user.getUserId(), user.getUsername(), user.getEmail(), user.getName(), user.getRole(),
				accessToken, refreshToken);
	}

	@PostMapping("/google-login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login with Google ID token", description = "Verifies Google ID token, syncs user in DB, and returns profile.")
	public JwtAuthResponse googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
		GoogleUser googleUser = googleTokenVerifier.verify(request.idToken());
		if (googleUser.email() == null || googleUser.email().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google token missing email");
		}
		User user = userRepository.findByEmail(googleUser.email())
				.orElseGet(() -> createUserFromGoogle(googleUser));
		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user);
		return JwtAuthResponse.of(user.getUserId(), user.getUsername(), user.getEmail(), user.getName(), user.getRole(),
				accessToken, refreshToken);
	}

	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Refresh access token", description = "Issues a new access token using a valid refresh token.")
	public JwtAuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
		String refreshToken = request.refreshToken();
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
		}
		Long userId = jwtTokenProvider.parseUserId(refreshToken);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		String newAccess = jwtTokenProvider.generateAccessToken(user);
		String newRefresh = jwtTokenProvider.generateRefreshToken(user);
		return JwtAuthResponse.of(user.getUserId(), user.getUsername(), user.getEmail(), user.getName(), user.getRole(),
				newAccess, newRefresh);
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
				.role(com.example.itsme.domain.Role.USER)
				.build();
		return userRepository.save(newUser);
	}

	private User createUserFromGoogle(GoogleUser googleUser) {
		String email = googleUser.email();
		String username = email != null && email.contains("@")
				? email.substring(0, email.indexOf("@"))
				: "google_" + googleUser.sub();
		String name = googleUser.name() != null ? googleUser.name() : username;

		User newUser = User.builder()
				.email(email)
				.username(username)
				.password(passwordService.hash("google"))
				.name(name)
				.role(com.example.itsme.domain.Role.USER)
				.build();
		return userRepository.save(newUser);
	}
}
