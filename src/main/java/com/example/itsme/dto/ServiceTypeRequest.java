package com.example.itsme.dto;

import jakarta.validation.constraints.NotBlank;

public record ServiceTypeRequest(@NotBlank String name) {
}
