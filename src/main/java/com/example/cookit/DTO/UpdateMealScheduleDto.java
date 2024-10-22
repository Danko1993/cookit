package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record UpdateMealScheduleDto(

        @NotNull(message = "Meal schedule id must be provided.")
        UUID  mealScheduleId,
        Date date,
        List<UUID> mealIds


) {
}
