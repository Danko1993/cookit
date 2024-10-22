package com.example.cookit.mappers;

import com.example.cookit.DTO.DietPlanDto;
import com.example.cookit.entities.DietPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DietPlanMapper {

    DietPlanMapper INSTANCE = Mappers.getMapper(DietPlanMapper.class);

    DietPlan toEntity(DietPlanDto dietPlanDto);
    @Mapping(target = "appUserId", source = "appUser.id")
    DietPlanDto toDto(DietPlan dietPlan);
}
