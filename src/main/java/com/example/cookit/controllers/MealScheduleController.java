package com.example.cookit.controllers;

import com.example.cookit.DTO.MealScheduleDto;
import com.example.cookit.DTO.SendMealScheduleDto;
import com.example.cookit.DTO.UpdateMealScheduleDto;
import com.example.cookit.services.MealScheduleService;
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


    @PostMapping("/add")
    public ResponseEntity<String> addMealScheduleAndBindWitDietPlan(@RequestBody @Valid MealScheduleDto mealScheduleDto, BindingResult bindingResult) {
        log.info("Preparing to add and bind meal schedule");
        if (bindingResult.hasErrors() && bindingResult!=null ) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>("Validation errors :"+bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return mealScheduleService.addMealScheduleAndBindWithDietPlan(mealScheduleDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateMealSchedule(@RequestBody @Valid UpdateMealScheduleDto updateMealScheduleDto, BindingResult bindingResult) {
        log.info("Preparing to update and bind meal schedule");
        if (bindingResult.hasErrors() && bindingResult!=null ) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>("Validation errors :"+bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return mealScheduleService.updateMealSchedule(updateMealScheduleDto);
    }

    @GetMapping("/get_by_diet_plan")
    public ResponseEntity<List<SendMealScheduleDto>> getMealScheduleByDietPlan(@RequestParam("id") UUID id) {
        log.info("Preparing to get all meal schedules by diet plan with id {}", id);
        return mealScheduleService.getMealSchedulesByDietPlanId(id);
    }
}
