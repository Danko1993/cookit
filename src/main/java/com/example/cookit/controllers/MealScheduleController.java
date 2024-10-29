package com.example.cookit.controllers;

import com.example.cookit.DTO.*;
import com.example.cookit.services.MealScheduleService;
import com.example.cookit.services.ValidationErrorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/meal_schedule")
public class MealScheduleController {
    @Autowired
    private MealScheduleService mealScheduleService;
    @Autowired
    private ValidationErrorService validationErrorService;


    @PostMapping("/add")
    public ResponseEntity<String> addMealScheduleAndBindWitDietPlan(@RequestBody @Valid MealScheduleDto mealScheduleDto, BindingResult result) {
        log.info("Preparing to add and bind meal schedule");
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        return mealScheduleService.addMealScheduleAndBindWithDietPlan(mealScheduleDto);
    }

    @PatchMapping("/add_meal")
    public ResponseEntity<String> addMealToMealSchedule(@RequestBody MealScheduleMealDto mealScheduleMealDto
            , BindingResult result) {
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        return mealScheduleService.addMealToMealSchedule(mealScheduleMealDto);
    }

    @PatchMapping("/delete_meal")
    public ResponseEntity<String> deleteMealFromMealSchedule(@RequestParam UUID mealId, @RequestParam UUID mealScheduleId) {
        return mealScheduleService.deleteMealFromMealSchedule(mealId, mealScheduleId);
    }


    @PatchMapping("/update")
    public ResponseEntity<String> updateMealSchedule(@RequestBody @Valid UpdateMealScheduleDto updateMealScheduleDto, BindingResult result) {
        log.info("Preparing to update and bind meal schedule");
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        return mealScheduleService.updateMealSchedule(updateMealScheduleDto);
    }

    @GetMapping("/get_by_diet_plan")
    public ResponseEntity<List<SendMealScheduleDto>> getMealScheduleByDietPlan(@RequestParam("id") UUID id) {
        log.info("Preparing to get all meal schedules by diet plan with id {}", id);
        return mealScheduleService.getMealSchedulesByDietPlanId(id);
    }
}
