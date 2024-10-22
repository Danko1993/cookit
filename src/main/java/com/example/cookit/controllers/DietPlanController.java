package com.example.cookit.controllers;

import com.example.cookit.DTO.DietPlanDto;
import com.example.cookit.DTO.UpdateDietPlanDto;
import com.example.cookit.services.DietPlanService;
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



    @PostMapping("/add")
    public ResponseEntity<String> addDietPlan(@RequestBody @Valid DietPlanDto dietPlanDto, BindingResult bindingResult) {
        log.info("Preparing diet plan to save in database");
        if (bindingResult.hasErrors() && bindingResult != null) {
            log.info("Validation errors: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>("Validation errors :"+ bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return dietPlanService.addDietPlan(dietPlanDto);
    }
    @PatchMapping("/update")
    public ResponseEntity<String> updateDietPlan(@RequestBody @Valid UpdateDietPlanDto updateDietPlanDto, BindingResult bindingResult) {
        log.info("Preparing diet plan to update in database");
        if (bindingResult.hasErrors() && bindingResult != null) {
            log.info("Validation errors: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>("Validation errors :"+ bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
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
