package com.example.cookit.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DietPlanAppUserDto(
        @NotNull(message = "Diet plan id must be provided")
        UUID dietPlanId,
        @NotNull(message = "App user id must be provided")
        UUID appUserId
) {
}
