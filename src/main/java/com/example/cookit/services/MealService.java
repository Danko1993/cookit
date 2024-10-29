package com.example.cookit.services;

import com.example.cookit.DTO.MealDto;
import com.example.cookit.DTO.MealIngredientDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public ResponseEntity<String> createMeal(MealDto mealDto) {
        log.info("Adding meal {} to database", mealDto.name());
        log.info("Checking if user with id: {} exist",mealDto.appUserId() );
        if (!appUserService.userExists(mealDto.appUserId())) {
            log.warn("Meal {} author not found", mealDto.name());
            return new ResponseEntity<>("User with id:" + mealDto.appUserId() + "not found.", HttpStatus.NOT_FOUND);
        }
        log.info("Meal {} author set to user with id: {}.", mealDto.name(), mealDto.appUserId());
        Meal meal = mealMapper.toEntity(mealDto);
        mealRepository.save(meal);
        log.info("Meal {} added to database", mealDto.name());
        return new ResponseEntity<>("Meal :"
                + mealDto.name() + " added to database.", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> addOrUpdateMealIngredient(MealIngredientDto mealIngredientDto, UUID mealId) {
        log.info("Checking if meal with id : {} exist", mealId);
        if (!mealRepository.existsById(mealId)) {
            log.warn("Meal with id {} does not exist", mealId);
            return new ResponseEntity<>("Meal with id " + mealId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        log.info("Checking if ingredient with id : {} exist", mealIngredientDto.id());
        if(!ingredientService.ingredientExist(mealIngredientDto.id())){
            log.warn("Ingredient with id {} does not exist", mealIngredientDto.id().toString());
            return new ResponseEntity<>("Ingredient with id " + mealIngredientDto.id() + " does not exist.", HttpStatus.NOT_FOUND);
        }
        Meal meal = mealRepository.findById(mealId).get();
        Ingredient ingredient = ingredientService.getById(mealIngredientDto.id());
        log.info("Adding ingredient {} to meal {}", ingredient.getName(), meal.getName());
        Map<Ingredient,Double> mealIngredients = meal.getIngredientsWithWeight();
        mealIngredients.put(ingredient, mealIngredientDto.amount());
        this.setMealNutrition(meal.getIngredientsWithWeight(),meal);
        mealRepository.save(meal);
        eventPublisher.publishMealUpdatedEvent(meal);
        return new ResponseEntity<String>("Added ingredient "+ingredient.getName()+" to meal "+meal.getName(), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> deleteMealIngredient(UUID ingredientId, UUID mealId) {
        log.info("Checking if meal with id : {} exist", mealId);
        if (!mealRepository.existsById(mealId)) {
            log.warn("Meal with id {} does not exist", mealId);
        }
        Meal meal = mealRepository.findById(mealId).get();
        log.info("Removing ingredient {} from meal {}", ingredientId, meal.getName());
        if (!ingredientService.ingredientExist(ingredientId)) {
            log.warn("Ingredient with id {} does not exist", ingredientId);
            return new ResponseEntity<>("Ingredient with id " + ingredientId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        Map<Ingredient,Double> mealIngredients = meal.getIngredientsWithWeight();
        mealIngredients.remove(ingredientId);
        meal.setIngredientsWithWeight(mealIngredients);
        this.setMealNutrition(meal.getIngredientsWithWeight(),meal);
        mealRepository.save(meal);
        eventPublisher.publishMealUpdatedEvent(meal);
        return new ResponseEntity<>("Ingredient "+ ingredientService.getById(ingredientId).getName()+" removed from meal "+meal.getName(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> addOrUpdatePhoto(String id, MultipartFile file) {
        log.info("Checking if meal with id : {} exist", id);
        UUID mealId = UUID.fromString(id);
        if (!mealRepository.existsById(mealId)) {
            log.warn("Meal with id {} does not exist", mealId);
            return new ResponseEntity<>("Meal with id " + mealId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        Meal meal = mealRepository.findById(mealId).get();
        String path;

            if (!meal.getImagePath().isEmpty()) {
                try {
                    fileStorageService.deleteFile(meal.getImagePath());
                } catch (IOException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
                }
                meal.setImagePath("");
            }
            try {
                path= fileStorageService.saveFile(file,"meal");
            }
            catch (IOException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            meal.setImagePath(path);
            mealRepository.save(meal);
            return new ResponseEntity<>("Photo saved", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> updateMeal(MealDto mealDto, UUID mealId) {
        log.info("Updating meal {} to database", mealDto.name());
        log.info("Checking if meal with id : {} exist", mealId);
        if (mealRepository.existsById(mealId)) {
            Meal meal = mealRepository.findMealById(mealId);
            if (mealDto.name() != meal.getName()) {
                meal.setName(mealDto.name());
                log.info("Changed meal name form {} to {}.}", meal.getName(), mealDto.name());
            }
            if (mealDto.description() != meal.getDescription()) {
                meal.setDescription(mealDto.description());
                log.info("Changed meal description form {} to {}.}", meal.getDescription(), mealDto.description());
            }
            mealRepository.save(meal);
            log.info("Meal {} updated successfully.", mealDto.name());
            return new ResponseEntity<>("Meal" + mealDto.name() +
                    "updated successfully", HttpStatus.OK);
        }
       log.warn("Meal with id {} not found.", mealId);
        return new ResponseEntity<>("Meal with id " + mealId+" not found.", HttpStatus.NOT_FOUND);
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

    public boolean checkMealsById(List<UUID> mealIds) {
       return mealIds.stream().allMatch(id ->  this.checkMealById(id));
    }

    public boolean checkMealById(UUID mealId) {
        return mealRepository.existsById(mealId);
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

    @EventListener
    public void handleIngredientUpdatedEvent(IngredientUpdatedEvent event) {
        this.updateAllNutrition();
    }

    public int countMeals(){
        return mealRepository.findAll().size();
    }
}
