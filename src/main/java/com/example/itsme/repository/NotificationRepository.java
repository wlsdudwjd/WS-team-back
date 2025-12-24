package com.example.itsme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUserUserId(Long userId);

	List<Notification> findByUserEmail(String email);
}
