package com.example.cookit.DTO;

import com.example.cookit.entities.AppUser;
import com.example.cookit.entities.MealSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
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
