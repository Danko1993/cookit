package com.example.cookit.services;

import com.example.cookit.DTO.MealDto;
import com.example.cookit.DTO.UpdateMealDto;
import com.example.cookit.entities.AppUser;
import com.example.cookit.entities.Ingredient;
import com.example.cookit.entities.Meal;
import com.example.cookit.mappers.MealMapper;
import com.example.cookit.repositories.AppUserRepository;
import com.example.cookit.repositories.IngredientRepository;
import com.example.cookit.repositories.MealRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class MealService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private final MealMapper mealMapper= MealMapper.INSTANCE;

    @Transactional
    public ResponseEntity<String> createMeal (MealDto mealDto) {
        log.info("Adding meal {} to database", mealDto.name());
        Meal meal = mealMapper.toEntity(mealDto);
        AppUser appUser = appUserRepository.findAppUserById(mealDto.appUserId());
        if (appUser != null) {
            log.info("Meal {} author set to {}.", mealDto.name(), appUser.getUsername());
            meal.setAppUser(appUser);
        }else {
            log.warn("Meal {} author not found", mealDto.name());
            return new ResponseEntity<>("User with id:"+mealDto.appUserId()+"not found.", HttpStatus.NOT_FOUND);
        }
        log.info("Preparing ingredients list");
        Map<Ingredient,Double> ingredientsWithWeight = new HashMap<>();
        for (Map.Entry<UUID,Double> entry : mealDto.ingredientsWithWeightDto().entrySet()){
            UUID ingredientId = entry.getKey();
            Double weight = entry.getValue();
            Ingredient ingredient = ingredientRepository.findIngredientById(ingredientId);
            if (ingredient != null) {
                ingredientsWithWeight.put(ingredient, weight);
                log.info("Ingredient {} added to meal", ingredient.getName());
            }else {
                log.warn("Ingredient with id:{} not found", ingredientId);
                return new ResponseEntity<>("Ingredient with id:"+ingredientId+"not found.", HttpStatus.NOT_FOUND);
            }
            meal.setIngredientsWithWeight(ingredientsWithWeight);
            mealRepository.save(meal);
        }
        log.info("Meal {} added to database", mealDto.name());
        return new ResponseEntity<>("Meal :"
                +mealDto.name()+" added to database.", HttpStatus.CREATED);
    }
    @Transactional
    public ResponseEntity<String> createManyMeals(List<MealDto> mealDtoList) {
        log.info("Adding meals to database");
        for (MealDto mealDto : mealDtoList) {
            this.createMeal(mealDto);
            log.info("Meal {} added to database", mealDto.name());
        }
        log.info("Meals added to database");
        return new ResponseEntity<>("Meals added to database.", HttpStatus.CREATED);
    }
    @Transactional
    public ResponseEntity<String> updateMeal (UpdateMealDto updateMealDto) {
        log.info("Updating meal {} to database", updateMealDto.name());
        Meal meal = mealRepository.findMealById(updateMealDto.mealId());
        if (meal != null) {
            if (updateMealDto.name() != meal.getName()) {
                meal.setName(updateMealDto.name());
                log.info("Changed meal name form {} to {}.}", meal.getName(), updateMealDto.name());
            }
            if (updateMealDto.description() != meal.getDescription()) {
                meal.setDescription(updateMealDto.description());
                log.info("Changed meal description form {} to {}.}", meal.getDescription(), updateMealDto.description());
            }
            Map<UUID,Double> ingredientsWithWeightDto = updateMealDto.ingredientsWithWeightDto();
            Map<Ingredient,Double> ingredientsWithWeight= meal.getIngredientsWithWeight();
           if(!ingredientsWithWeightDto.isEmpty()) {
               ingredientsWithWeight.clear();
               for (Map.Entry<UUID,Double> entry : ingredientsWithWeightDto.entrySet()){
                    Ingredient ingredient = ingredientRepository.findIngredientById(entry.getKey());
                    Double weight = entry.getValue();
                    ingredientsWithWeight.put(ingredient, weight);
               }
           }
           meal.setIngredientsWithWeight(ingredientsWithWeight);
           log.info("Updated meal {} ingredients list", updateMealDto.name());
        }
        mealRepository.save(meal);
        log.info("Meal {} updated successfully.", updateMealDto.name());
        return new ResponseEntity<>("Meal"+updateMealDto.name()+
                "updated successfully", HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<String> deleteMeal (UUID mealId) {
        log.info("Checking if meal with id {} exists", mealId);
        if (mealRepository.findMealById(mealId)!= null){
            mealRepository.deleteMealById(mealId);
            log.info("Meal with id {} deleted successfully.", mealId);
            return new ResponseEntity<>("Meal with id "+mealId+" deleted successfully", HttpStatus.OK);
        }
        log.warn("Meal with id {} not found", mealId);
        return new ResponseEntity<>("Meal with id "+mealId+" not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<MealDto>> getAllMeals () {
        List<Meal> allMeals = mealRepository.findAll();
        List<MealDto> mealDtos = new ArrayList<>();
        for (Meal meal : allMeals) {
            mealDtos.add(mealMapper.toDto(meal));
        }
        return new ResponseEntity<>(mealDtos, HttpStatus.OK);
    }

    public ResponseEntity<List<MealDto>> getMealsByUser (UUID userId) {
        log.info("Checking if user with id {} exists", userId);
        if (appUserRepository.findAppUserById(userId) != null) {
            List<Meal> mealsByUser = appUserRepository.findAppUserById(userId).getMeals();
            List<MealDto> mealDtos = new ArrayList<>();
            for (Meal meal : mealsByUser) {
                mealDtos.add(mealMapper.toDto(meal));
            }
            log.info("Meals from user {} found", appUserRepository.findAppUserById(userId).getUsername());
            return new ResponseEntity<>(mealDtos, HttpStatus.OK);
        }
        log.warn("User with id {} not found", userId);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<MealDto> getMealById (UUID mealId) {
        log.info("Checking if meal with id {} exists", mealId);
        if (mealRepository.findMealById(mealId) != null) {
            Meal meal = mealRepository.findMealById(mealId);
            MealDto mealDto = mealMapper.toDto(meal);
            return new ResponseEntity<>(mealDto, HttpStatus.OK);
        }
        log.warn("Meal with id {} not found", mealId);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
