package com.example.itsme.dto;

import jakarta.validation.constraints.NotNull;

public record CartRequest(@NotNull Long userId) {
}
