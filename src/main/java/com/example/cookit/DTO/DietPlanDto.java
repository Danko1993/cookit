package com.example.cookit.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.UUID;

public record DietPlanDto(
        @NotBlank(message = "Name must be provided")
        String name,
        @NotNull(message = "AppUserId must be provided")
        UUID appUserId,
        @NotNull(message = "Daily calories must be provided")
        Double dailyCalories
) {
}
