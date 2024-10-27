package com.example.cookit.mappers;

import com.example.cookit.DTO.IngredientDto;
import com.example.cookit.DTO.UpdateIngredientDto;
import com.example.cookit.entities.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IngredientMapper {
    IngredientMapper INSTANCE = Mappers.getMapper(IngredientMapper.class);
    Ingredient toEntity(IngredientDto ingredientDto);
    IngredientDto toDto(Ingredient ingredient);
    void updateIngredient(UpdateIngredientDto updateIngredientDto, @MappingTarget Ingredient ingredient);
}
