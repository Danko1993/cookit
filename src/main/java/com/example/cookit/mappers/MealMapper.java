package com.example.cookit.mappers;

import com.example.cookit.DTO.MealDto;
import com.example.cookit.entities.Ingredient;
import com.example.cookit.entities.Meal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface MealMapper {
    MealMapper INSTANCE = Mappers.getMapper(MealMapper.class);
    @Mapping(target = "appUser", ignore = true)
    @Mapping(target = "ingredientsWithWeight", ignore = true)
    Meal toEntity(MealDto mealDto);

    @Mapping(source = "appUser.id",target = "appUserId")
    @Mapping(target = "ingredientsWithWeightDto", source = "ingredientsWithWeight")
    MealDto toDto(Meal meal);

    default Map<UUID,Double> mapIngredientsWithWeight(Map<Ingredient,Double> ingredientsWithWeight) {
        Map<UUID,Double> ingredientsWithWeightDto = new HashMap<>();
        if (ingredientsWithWeight != null) {
            for (Map.Entry<Ingredient,Double> entry : ingredientsWithWeight.entrySet()) {
                ingredientsWithWeightDto.put(entry.getKey().getId(), entry.getValue());
            }
        }
        return ingredientsWithWeightDto;
    }

}
