package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.itsme.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Page<Notification> findByUserUserId(Long userId, Pageable pageable);

	Page<Notification> findByUserEmail(String email, Pageable pageable);
}
