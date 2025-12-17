package com.example.itsme.dto;

import jakarta.validation.constraints.Min;

public record CartItemQuantityRequest(@Min(1) int quantity) {
}
