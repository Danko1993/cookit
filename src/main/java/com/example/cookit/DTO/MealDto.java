package com.example.cookit.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record MealDto(
        @NotNull(message = "User id must be provided.")
        UUID appUserId,
        @NotBlank(message = "Meal have to be named.")
        String name,
        @NotBlank(message = "Meal needs a description.")
        @Size(min = 50, message = "Description needs to be at least 50 characters long")
        String description
) {
}
