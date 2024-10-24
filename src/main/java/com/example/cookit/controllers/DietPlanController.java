package com.example.cookit.controllers;

import com.example.cookit.DTO.DietPlanDto;
import com.example.cookit.DTO.UpdateDietPlanDto;
import com.example.cookit.services.DietPlanService;
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
@RequestMapping("diet_plan")
public class DietPlanController {
    @Autowired
    private DietPlanService dietPlanService;
    @Autowired
    private ValidationErrorService validationErrorService;



    @PostMapping("/add")
    public ResponseEntity<String> addDietPlan(@RequestBody @Valid DietPlanDto dietPlanDto, BindingResult result) {
        log.info("Preparing diet plan to save in database");
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        return dietPlanService.addDietPlan(dietPlanDto);
    }
    @PatchMapping("/update")
    public ResponseEntity<String> updateDietPlan(@RequestBody @Valid UpdateDietPlanDto updateDietPlanDto, BindingResult result) {
        log.info("Preparing diet plan to update in database");
        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        return dietPlanService.updateDietPlan(updateDietPlanDto);
    }
    @GetMapping("/get_by_user")
    public ResponseEntity<List<DietPlanDto>> getDietPlanByUser(@RequestParam("id") UUID userId) {
        log.info("Preparing diet plan to get all diet plans for user with id {}", userId);
        return dietPlanService.getDietPlansByUserId(userId);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDietPlan(@RequestParam("id") UUID dietPlanId) {
        log.info("Preparing diet plan to delete in database");
        return dietPlanService.deleteDietPlan(dietPlanId);
    }
}
