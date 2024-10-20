package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record UpdateMealDto(
        @NotNull(message = "Meal id must be provided")
        UUID mealId,
        String name,
        String description,
        Map<UUID,Double> ingredientsWithWeightDto
) {
}
