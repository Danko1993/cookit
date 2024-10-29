package com.example.cookit.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ShoppingListDto(
        @NotBlank(message = "Name must be provided.")
        String name,
        @NotNull(message = "AppUserId must be provided.")
        UUID appUserId
) {
}
