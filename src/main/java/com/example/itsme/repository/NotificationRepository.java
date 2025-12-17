package com.example.itsme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
