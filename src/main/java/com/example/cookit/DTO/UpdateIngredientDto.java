package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateIngredientDto(
        @NotNull(message = "Id must be provided.")
        UUID id,
        String name,
        Double caloriesPer100g,
        Double carbsPer100g,
        Double proteinPer100g,
        Double fatsPer100g
) {
}
