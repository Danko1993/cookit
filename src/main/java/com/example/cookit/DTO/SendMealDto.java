package com.example.cookit.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMealDto {

    private UUID appUserId;
    private String name;
    private String description;
    private Map<UUID,Double> ingredientsWithWeightDto;

    private double calories;

    private double carbs;

    private double proteins;

    private double fats;


}
