package com.example.itsme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	@GetMapping
	public List<Notification> getNotifications(@RequestParam(required = false) Long userId) {
		if (userId == null) {
			return notificationRepository.findAll();
		}
		return notificationRepository.findByUserUserId(userId);
	}

	@GetMapping("/{id}")
	public Notification getNotification(@PathVariable Long id) {
		return fetchNotification(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Notification createNotification(@Valid @RequestBody NotificationRequest request) {
		User user = fetchUser(request.userId());
		Notification notification = Notification.builder()
				.user(user)
				.title(request.title())
				.body(request.body())
				.build();
		return notificationRepository.save(notification);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteNotification(@PathVariable Long id) {
		Notification notification = fetchNotification(id);
		notificationRepository.delete(notification);
	}

	private Notification fetchNotification(Long id) {
		return notificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}
}
