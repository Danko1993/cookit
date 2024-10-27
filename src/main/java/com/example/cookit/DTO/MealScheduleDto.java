package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record MealScheduleDto(
        @NotNull(message = "Diet plan id must be provided.")
        UUID dietPlanId,
        @NotNull(message = "Date must be provided")
        Date date,
        @NotNull(message = "Meal ids must be provided")
        List<UUID> mealsIds
) {
}
