package com.example.cookit.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



public record IngredientDto(
        @NotBlank(message = "Name can not be empty.")
        String name,
        @NotNull(message = "Calories per 100g must to be provided.")
        Double caloriesPer100g,
        @NotNull(message = "Carbs per 100g must be provided.")
        Double carbsPer100g,
        @NotNull(message = "Protein per 100g must be provided.")
        Double proteinPer100g,
        @NotNull (message = "Fats per 100g must be provided.")
        Double fatsPer100g) {
}
