package com.example.cookit.controllers;

import com.example.cookit.DTO.MealDto;
import com.example.cookit.DTO.UpdateMealDto;
import com.example.cookit.services.MealService;
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
@RequestMapping("/meal")
public class MealController {

    @Autowired
    private MealService mealService;

    @PostMapping("/create")
    public ResponseEntity<String> createMeal(@RequestBody @Valid MealDto mealDto, BindingResult results) {
        if (results.hasErrors() && results !=null) {
            log.warn("Validation Errors: {}", results.getAllErrors());
            return new ResponseEntity<>("Validation errors:"+results.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        log.info("Proceeding to create meal: {}", mealDto.name());
        return mealService.createMeal(mealDto);
    }

    @PostMapping("/create_many")
    public ResponseEntity<String> createMeals(@RequestBody @Valid List<MealDto> mealDtos, BindingResult results) {
        if (results.hasErrors() && results !=null) {
            log.warn("Validation Errors: {}", results.getAllErrors());
            return new ResponseEntity<>("Validation errors:"+results.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        log.info("Proceeding to create meals: {}", mealDtos.toString());
        return mealService.createManyMeals(mealDtos);
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateMeal(@RequestBody @Valid UpdateMealDto updateMealDto, BindingResult results) {
        if (results.hasErrors() && results !=null) {
            log.warn("Validation Errors: {}", results.getAllErrors());
            return new ResponseEntity<>("Validation errors:"+results.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        log.info("Proceeding to update meal to: {}", updateMealDto.name());
        return mealService.updateMeal(updateMealDto);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMeal(@RequestParam("id") UUID mealId) {
        return mealService.deleteMeal(mealId);
    }

    @GetMapping("/get_all")
    public ResponseEntity<List<MealDto>> getAllMeals() {
        log.info("Getting all meals");
        return mealService.getAllMeals();
    }

    @GetMapping("/get_by_user")
    public ResponseEntity<List<MealDto>> getMealsByUser(@RequestParam("id") UUID userId) {
        log.info("Getting all meals by user with id: {}", userId);
        if (userId == null) {
            log.warn("User id must be provided");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return mealService.getMealsByUser(userId);
    }

    @GetMapping("/get_by_id")
    public ResponseEntity<MealDto> getMealById(@RequestParam("id") UUID mealId) {
        log.info("Getting meal with id: {}", mealId);
        return mealService.getMealById(mealId);
    }
}
