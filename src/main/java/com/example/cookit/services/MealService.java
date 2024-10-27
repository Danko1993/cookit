package com.example.cookit.services;

import com.example.cookit.DTO.MealDto;
import com.example.cookit.DTO.SendMealDto;
import com.example.cookit.DTO.UpdateMealDto;
import com.example.cookit.entities.AppUser;
import com.example.cookit.entities.Ingredient;
import com.example.cookit.entities.Meal;
import com.example.cookit.events.EventPublisher;
import com.example.cookit.events.IngredientUpdatedEvent;
import com.example.cookit.events.MealUpdatedEvent;
import com.example.cookit.mappers.MealMapper;
import com.example.cookit.repositories.AppUserRepository;
import com.example.cookit.repositories.MealRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MealService {
    @Autowired
    private MealRepository mealRepository;
    private final MealMapper mealMapper = MealMapper.INSTANCE;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private EventPublisher eventPublisher;

    @Transactional
    public ResponseEntity<String> createMeal(MealDto mealDto) {
        log.info("Adding meal {} to database", mealDto.name());
        Meal meal = mealMapper.toEntity(mealDto);
        if (!appUserService.userExists(mealDto.appUserId())) {
            log.warn("Meal {} author not found", mealDto.name());
            return new ResponseEntity<>("User with id:" + mealDto.appUserId() + "not found.", HttpStatus.NOT_FOUND);
        }
        log.info("Meal {} author set to user with id: {}.", mealDto.name(), mealDto.appUserId());
        log.info("Preparing ingredients list");
        boolean ingredientsExist = ingredientService.ingredientsExist(mealDto.ingredientsWithWeightDto().keySet()
        .stream().collect(Collectors.toList()));
        if (!ingredientsExist) {
            log.warn("At least one ingredient not found");
            return new ResponseEntity<>("At least one ingredient not found in data base.", HttpStatus.NOT_FOUND);
        }
        Map<Ingredient, Double> ingredientsWithWeight = this.mapIngredientsWithWeightByIngredientId(mealDto.ingredientsWithWeightDto());
        meal.setIngredientsWithWeight(ingredientsWithWeight);
        this.setMealNutrition(ingredientsWithWeight, meal);
        mealRepository.save(meal);
        log.info("Meal {} added to database", mealDto.name());
        return new ResponseEntity<>("Meal :"
                + mealDto.name() + " added to database.", HttpStatus.CREATED);
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
    public ResponseEntity<String> updateMeal(UpdateMealDto updateMealDto) {
        log.info("Updating meal {} to database", updateMealDto.name());
        if (mealRepository.findMealById(updateMealDto.mealId())!=null) {
            Meal meal = mealRepository.findMealById(updateMealDto.mealId());
            if (updateMealDto.name() != meal.getName()) {
                meal.setName(updateMealDto.name());
                log.info("Changed meal name form {} to {}.}", meal.getName(), updateMealDto.name());
            }
            if (updateMealDto.description() != meal.getDescription()) {
                meal.setDescription(updateMealDto.description());
                log.info("Changed meal description form {} to {}.}", meal.getDescription(), updateMealDto.description());
            }
            Map<Ingredient, Double> ingredientsWithWeight = meal.getIngredientsWithWeight();
            if (!updateMealDto.ingredientsWithWeightDto().isEmpty()) {
                ingredientsWithWeight.clear();
                Map<Ingredient,Double> newIngredientsWithWeight = this.mapIngredientsWithWeightByIngredientId(updateMealDto.ingredientsWithWeightDto());
                ingredientsWithWeight.putAll(newIngredientsWithWeight);
            }
            meal.setIngredientsWithWeight(ingredientsWithWeight);
            this.setMealNutrition(ingredientsWithWeight,meal);
            log.info("Updated meal {} ingredients list", updateMealDto.name());
            eventPublisher.publishMealUpdatedEvent(meal);
            mealRepository.save(meal);
            log.info("Meal {} updated successfully.", updateMealDto.name());
            return new ResponseEntity<>("Meal" + updateMealDto.name() +
                    "updated successfully", HttpStatus.OK);
        }

       log.warn("Meal with id {} not found.",updateMealDto.mealId());
        return new ResponseEntity<>("Meal with id " + updateMealDto.mealId()+" not found.", HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<String> deleteMeal(UUID mealId) {
        log.info("Checking if meal with id {} exists", mealId);
        if (mealRepository.findMealById(mealId) != null) {
            mealRepository.deleteMealById(mealId);
            log.info("Meal with id {} deleted successfully.", mealId);
            return new ResponseEntity<>("Meal with id " + mealId + " deleted successfully", HttpStatus.OK);
        }
        log.warn("Meal with id {} not found", mealId);
        return new ResponseEntity<>("Meal with id " + mealId + " not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<SendMealDto>> getAllMeals() {
        List<Meal> allMeals = mealRepository.findAll();
        List<SendMealDto> mealDtos = new ArrayList<>();
        for (Meal meal : allMeals) {
            mealDtos.add(mealMapper.toSendMealDto(meal));
        }
        return new ResponseEntity<>(mealDtos, HttpStatus.OK);
    }

    public ResponseEntity<List<SendMealDto>> getMealsByUser(UUID userId) {
        log.info("Checking if user with id {} exists", userId);
        List<SendMealDto> mealDtos = new ArrayList<>();
        if (appUserService.userExists(userId)) {
            List<Meal> mealsByUser = new ArrayList<>();
            List<Meal> allMeals = mealRepository.findAll();
            for (Meal meal : allMeals) {
                mealsByUser.add(meal);
            }
            for (Meal meal : mealsByUser) {
                mealDtos.add(mealMapper.toSendMealDto(meal));
            }
            log.info("Meals from user {} found", appUserService.getUserById(userId).getUsername());
            return new ResponseEntity<>(mealDtos, HttpStatus.OK);
        }
        log.warn("User with id {} not found", userId);
        return new ResponseEntity<>(mealDtos, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<SendMealDto> getMealById(UUID mealId) {
        log.info("Checking if meal with id {} exists", mealId);
        if (mealRepository.findMealById(mealId) != null) {
            Meal meal = mealRepository.findMealById(mealId);
            SendMealDto mealDto = mealMapper.toSendMealDto(meal);
            return new ResponseEntity<>(mealDto, HttpStatus.OK);
        }
        log.warn("Meal with id {} not found", mealId);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public Meal getById(UUID mealId) {
        return mealRepository.findMealById(mealId);
    }


    public List<Meal> getMealsById(List<UUID> mealIds) {
        List<Meal> meals = new ArrayList<>();
        for (UUID id : mealIds) {
            Meal meal = this.getById(id);
            meals.add(meal);
        }
        return meals;
    }

    public boolean validateMealsIds(List<UUID> mealsIds) {

        for (UUID mealId : mealsIds) {
            if (!mealRepository.findById(mealId).isPresent()) {
                return false;
            }
        }
        return true;
    }

    public double calculateNutrition(Map<Ingredient, Double> ingredients,Function<Ingredient,Double> valueExtractor){
        return ingredients.entrySet().stream().mapToDouble(entry -> {
            Ingredient ingredient = entry.getKey();
            Double weightInGrams = entry.getValue();
            return (valueExtractor.apply(ingredient) * weightInGrams) / 100;
    }).sum();
    }

    public void setMealNutrition(Map<Ingredient, Double> ingredients, Meal meal) {
        meal.setCalories(calculateNutrition(ingredients,Ingredient::getCaloriesPer100g));
        meal.setCarbs(calculateNutrition(ingredients,Ingredient::getCarbsPer100g));
        meal.setFats(calculateNutrition(ingredients,Ingredient::getFatsPer100g));
        meal.setProteins(calculateNutrition(ingredients,Ingredient::getProteinPer100g));
    }

    public void updateAllNutrition (){
        List<Meal> allMeals = mealRepository.findAll();
        for (Meal meal : allMeals) {
            this.setMealNutrition(meal.getIngredientsWithWeight(),meal);
        }
        eventPublisher.publishMealUpdatedEvent(allMeals.get(0));
    }

    public Map<Ingredient, Double> mapIngredientsWithWeightByIngredientId(Map<UUID, Double> ingredientsWithWeight) {
        Map<Ingredient, Double> ingredients = new HashMap<>();
        for (Map.Entry<UUID, Double> entry : ingredientsWithWeight.entrySet()) {
            UUID ingredientId = entry.getKey();
            Double weight = entry.getValue();
            Ingredient ingredient = ingredientService.getById(ingredientId);
            ingredients.put(ingredient, weight);
        }
        return ingredients;
    }
    @EventListener
    public void handleIngredientUpdatedEvent(IngredientUpdatedEvent event) {
        this.updateAllNutrition();
    }

    public int countMeals(){
        return mealRepository.findAll().size();
    }
}
