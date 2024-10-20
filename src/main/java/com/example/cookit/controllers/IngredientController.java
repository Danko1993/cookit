package com.example.cookit.controllers;

import com.example.cookit.DTO.IngredientDto;
import com.example.cookit.DTO.UpdateIngredientDto;
import com.example.cookit.entities.Ingredient;
import com.example.cookit.services.IngredientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ingredient")
@Slf4j
public class IngredientController {
    @Autowired
    private IngredientService ingredientService;

    @PostMapping("/add")
    public ResponseEntity<String> addIngredient(@RequestBody @Valid IngredientDto ingredientDto, BindingResult result) {
        log.info("Preparing ingredient {} to save in data base", ingredientDto.name());
        if (result!=null && result.hasErrors()) {
            log.warn("Validation failed for ingredient {} due to :{}", ingredientDto.name(), result.getAllErrors());
            return ResponseEntity.badRequest().body("Validation erros :" + result.getAllErrors().toString());
        }
        log.info("Validation for ingredient {} successful", ingredientDto.name());
        return ingredientService.addIngredient(ingredientDto);
    }

    @PostMapping("/add_many")
    public ResponseEntity<String> addIngredients(@RequestBody @Valid List<IngredientDto> ingredientDtos, BindingResult result) {
        log.info("Preparing ingredients {} to save in data base", ingredientDtos.toString());
        if (result!=null && result.hasErrors()) {
            log.warn("Validation erros : {}", result.getAllErrors());
            return ResponseEntity.badRequest().body("Validation erros :" + result.getAllErrors().toString());
        }
        log.info("Validation for ingredients {} successful", ingredientDtos.toString());
        return ingredientService.addIngredients(ingredientDtos);
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateIngredient(@RequestBody @Valid UpdateIngredientDto updateIngredientDto, BindingResult result) {
        log.info("Preparing ingredient {} to update in data base", updateIngredientDto.name());
        if (result!=null && result.hasErrors()) {
            log.warn("Validation failed for ingredient {} due to :{}", updateIngredientDto.name(), result.getAllErrors());
            return ResponseEntity.badRequest().body("Validation erros :" + result.getAllErrors().toString());
        }
        log.info("Validation for ingredient {} successful", updateIngredientDto.name());
        return ingredientService.updateIngredient(updateIngredientDto);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteIngredient(@RequestParam("id") UUID id) {
        log.info("Preparing ingredient {} to delete in data base", id);
        return ingredientService.deleteIngredient(id);
    }
    @GetMapping("/get_all")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        log.info("Preparing ingredient list");
        return ingredientService.getAllIngredients();
    }
    @GetMapping("/get")
    public ResponseEntity<Ingredient> getIngredient(@RequestParam("id") UUID id) {
        log.info("Preparing ingredient with id {} to retrieve in data base", id);
        return ingredientService.getIngredientById(id);
    }
}
