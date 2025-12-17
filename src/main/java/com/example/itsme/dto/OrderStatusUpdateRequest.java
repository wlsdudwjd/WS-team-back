package com.example.itsme.dto;

import com.example.itsme.domain.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(@NotNull OrderStatus status) {
}
