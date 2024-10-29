package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MealIngredientDto(
        @NotNull(message = "Ingredient id must be provided.")
        UUID id,
        @NotNull(message = "Ingredient weight must be provided.")
        Double amount) {
}
