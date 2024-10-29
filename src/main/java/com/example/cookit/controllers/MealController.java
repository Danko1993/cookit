package com.example.cookit.controllers;

import com.example.cookit.DTO.*;
import com.example.cookit.services.MealService;
import com.example.cookit.services.ValidationErrorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/meal")
public class MealController {

    @Autowired
    private MealService mealService;
    @Autowired
    private ValidationErrorService validationErrorService;

    @PostMapping("/create")
    public ResponseEntity<String> createMeal(@RequestBody @Valid MealDto mealDto, BindingResult result) {
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        log.info("Proceeding to create meal: {}", mealDto.name());
        return mealService.createMeal(mealDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateMeal(@RequestBody @Valid MealDto mealDto,
                                            @RequestParam("id") UUID mealId,
                                             BindingResult result) {
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        log.info("Proceeding to update meal to: {}", mealDto.name());
        return mealService.updateMeal(mealDto,mealId);
    }
    @PatchMapping("/add_ingredient")
    public ResponseEntity<String> addIngredient(@RequestBody @Valid MealIngredientDto mealIngredientDto,
                                                @RequestParam("id") UUID mealId, BindingResult result) {
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        log.info("Proceeding to add ingredient to meal {}", mealId);
        return mealService.addOrUpdateMealIngredient(mealIngredientDto,mealId);
    }
    @DeleteMapping("/delete_ingredient")
    public ResponseEntity<String> deleteIngredient(@RequestParam("ingredientId") UUID ingredientId,
                                                   @RequestParam("mealId") UUID mealId) {
        log.info("Proceeding to delete ingredient to meal {}", mealId);
        return mealService.deleteMealIngredient(ingredientId,mealId);
    }


    @PostMapping(value = "/add_photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPhoto(@RequestPart("file") MultipartFile file,
                                           @RequestPart("id")String mealId){
        return mealService.addOrUpdatePhoto(mealId,file);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMeal(@RequestParam("id") UUID mealId) {
        return mealService.deleteMeal(mealId);
    }

    @GetMapping("/get_all")
    public ResponseEntity<List<SendMealDto>> getAllMeals() {
        log.info("Getting all meals");
        return mealService.getAllMeals();
    }

    @GetMapping("/get_by_user")
    public ResponseEntity<List<SendMealDto>> getMealsByUser(@RequestParam("id") UUID userId) {
        log.info("Getting all meals by user with id: {}", userId);
        if (userId == null) {
            log.warn("User id must be provided");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return mealService.getMealsByUser(userId);
    }

    @GetMapping("/get_by_id")
    public ResponseEntity<SendMealDto> getMealById(@RequestParam("id") UUID mealId) {
        log.info("Getting meal with id: {}", mealId);
        return mealService.getMealById(mealId);
    }
}
