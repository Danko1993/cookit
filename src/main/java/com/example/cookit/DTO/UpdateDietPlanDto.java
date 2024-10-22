package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateDietPlanDto(
        @NotNull(message = "Diet plan id must be provided.")
        UUID id,
        String name,
        Double dailyCalories
) {
}
