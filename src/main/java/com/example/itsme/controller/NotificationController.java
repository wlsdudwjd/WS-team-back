package com.example.itsme.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Notification;
import com.example.itsme.domain.User;
import com.example.itsme.dto.NotificationRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.NotificationRepository;
import com.example.itsme.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
@Tag(name = "Notifications", description = "Send and retrieve user notifications")
public class NotificationController {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "알림 목록 조회", description = "사용자 ID/이메일로 알림을 페이지네이션 조회")
	public Page<Notification> getNotifications(@RequestParam(required = false) Long userId,
			@RequestParam(required = false) String userEmail,
			Pageable pageable) {
		User user = resolveUser(userId, userEmail);
		return notificationRepository.findByUserUserId(user.getUserId(), pageable);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "알림 단건 조회", description = "notificationId로 알림 상세 조회")
	public Notification getNotification(@PathVariable Long id) {
		return fetchNotification(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "알림 생성", description = "사용자에게 알림을 생성합니다")
	public Notification createNotification(@Valid @RequestBody NotificationRequest request) {
		User user = resolveUser(request.userId(), request.userEmail());
		Notification notification = Notification.builder()
				.user(user)
				.title(request.title())
				.body(request.body())
				.build();
		return notificationRepository.save(notification);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@Operation(summary = "알림 삭제", description = "notificationId로 알림을 삭제합니다")
	public void deleteNotification(@PathVariable Long id) {
		Notification notification = fetchNotification(id);
		notificationRepository.delete(notification);
	}

	private Notification fetchNotification(Long id) {
		return notificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
	}

	private User resolveUser(Long userId, String userEmail) {
		if (userId != null) {
			return userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		}
		if (userEmail != null && !userEmail.isBlank()) {
			return userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
		}
		throw new ResourceNotFoundException("User identifier required");
	}
}
