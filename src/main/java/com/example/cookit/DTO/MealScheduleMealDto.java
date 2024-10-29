package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MealScheduleMealDto(
        @NotNull(message = "Meal id must be provided" )
        UUID mealId,
        @NotNull(message = "Meal schedule id must be provided")
        UUID mealScheduleId) {
}
