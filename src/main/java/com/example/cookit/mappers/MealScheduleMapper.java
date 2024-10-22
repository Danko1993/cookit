package com.example.cookit.mappers;

import com.example.cookit.DTO.MealDto;
import com.example.cookit.DTO.MealScheduleDto;
import com.example.cookit.DTO.SendMealScheduleDto;
import com.example.cookit.entities.MealSchedule;
import com.example.cookit.services.MealService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface MealScheduleMapper {


    MealScheduleMapper INSTANCE = Mappers.getMapper(MealScheduleMapper.class);

    @Mapping(target = "dietPlan.id", source = "dietPlanId")
    @Mapping(target = "meals", ignore = true)
    MealSchedule toEntity(MealScheduleDto mealScheduleDto);

  SendMealScheduleDto toSendMealScheduleDto(MealSchedule mealSchedule);
}