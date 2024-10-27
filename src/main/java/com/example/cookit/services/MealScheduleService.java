package com.example.cookit.services;

import com.example.cookit.DTO.*;
import com.example.cookit.entities.DietPlan;
import com.example.cookit.entities.Meal;
import com.example.cookit.entities.MealSchedule;
import com.example.cookit.events.IngredientUpdatedEvent;
import com.example.cookit.events.MealUpdatedEvent;
import com.example.cookit.mappers.MealMapper;
import com.example.cookit.mappers.MealScheduleMapper;
import com.example.cookit.repositories.MealScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MealScheduleService {

    private final MealScheduleMapper mealScheduleMapper = MealScheduleMapper.INSTANCE;

    @Autowired
    private MealScheduleRepository mealScheduleRepository;
    @Autowired
    private DietPlanService dietPlanService;
    @Autowired
    private MealService mealService;

    private final MealMapper   mealMapper = MealMapper.INSTANCE;


    @Transactional
    public ResponseEntity<String> addMealScheduleAndBindWithDietPlan(MealScheduleDto mealScheduleDto) {
        log.info("Checking id diet plan with id {} exists.", mealScheduleDto.dietPlanId());
        if (dietPlanService.existById(mealScheduleDto.dietPlanId())) {
            log.info("Diet plan with id {} found.", mealScheduleDto.dietPlanId());
            boolean checkMeals = mealService.validateMealsIds(mealScheduleDto.mealsIds());
            if (checkMeals) {
                MealSchedule mealSchedule = mealScheduleMapper.toEntity(mealScheduleDto);
                List<Meal> meals = mealService.getMealsById(mealScheduleDto.mealsIds());
                this.setNutrition(mealSchedule, meals);
                mealSchedule.setMeals(meals);
                mealScheduleRepository.save(mealSchedule);
                log.info("Meal schedule added successfully.");
                return new ResponseEntity<>("Meal schedule added successfully.", HttpStatus.CREATED);
            }
            else {
                log.warn("At least one meal not found in database.");
                return new ResponseEntity<>("At least one meal not found in database.", HttpStatus.NOT_FOUND);
            }
        }
        log.warn("Diet plan with id {} not found.", mealScheduleDto.dietPlanId());
        return new ResponseEntity<>("Diet plan with id" + mealScheduleDto.dietPlanId() + " not found.", HttpStatus.NOT_FOUND);

    }

    @Transactional
    public ResponseEntity<String> updateMealSchedule(UpdateMealScheduleDto updateMealScheduleDto) {
        log.info("Checking if meal schedule with id {} exist", updateMealScheduleDto.mealScheduleId());
        if (mealScheduleRepository.findById(updateMealScheduleDto.mealScheduleId()).isPresent()) {
            log.info("Meal schedule with id {} found.", updateMealScheduleDto.mealScheduleId());
            MealSchedule existingMealSchedule = mealScheduleRepository.findById(updateMealScheduleDto.mealScheduleId()).get();
            existingMealSchedule.setDate(updateMealScheduleDto.date());
            boolean checkMeals = mealService.validateMealsIds(updateMealScheduleDto.mealIds());
            if (!checkMeals) {
                log.warn("At least one meal not found in database.");
                return new ResponseEntity<>("At least one meal not found in database.", HttpStatus.NOT_FOUND);
            }
            List<Meal> meals = mealService.getMealsById(updateMealScheduleDto.mealIds());
            this.setNutrition(existingMealSchedule, meals);
            existingMealSchedule.setMeals(meals);
            log.info("Meal schedule updated successfully.");
            mealScheduleRepository.save(existingMealSchedule);
            return new ResponseEntity<>("Meal schedule updated successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Meal schedule with id: " + updateMealScheduleDto.mealScheduleId() + " not found.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<SendMealScheduleDto>> getMealSchedulesByDietPlanId(UUID id) {
            DietPlan dietPlan = null;
            List<MealSchedule> mealSchedules = mealScheduleRepository.findAll();
            log.info("Meal schedules {}", mealSchedules);
            List<SendMealScheduleDto> result = new ArrayList<>();
            for (MealSchedule mealSchedule : mealSchedules) {
                if (mealSchedule.getDietPlanId().equals(id)) {
                SendMealScheduleDto sendMealScheduleDto = mealScheduleMapper.toSendMealScheduleDto(mealSchedule);
                List<Meal> meals = mealSchedule.getMeals();
                List<SendMealDto> mealDtoList = new ArrayList<>();
                    for (Meal meal : meals) {
                        SendMealDto sendMealDto = mealMapper.toSendMealDto(meal);
                        mealDtoList.add(sendMealDto);
                    }
                sendMealScheduleDto.setMeals(mealDtoList);
                result.add(sendMealScheduleDto);
            }}
            log.info("Meal schedules to be sent {}.", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public List<MealSchedule> getMealSchedulesById(List<UUID> ids) {
        log.info("Checking if provided meal schedules exist.");
        boolean schedulesPresent = this.checkMealSchedules(ids);
        List<MealSchedule> result = new ArrayList<>();
        if (schedulesPresent) {
            log.info("Meal schedules found successfully.");
            ids.stream().map(id -> mealScheduleRepository.findById(id).orElse(null)).forEach(result::add);
            return result;
        }
        return null;
    }

    public boolean checkMealSchedules(List<UUID> mealIds) {
        return mealIds.stream().allMatch(id -> this.checkMealSchedule(id));
    }

    public boolean checkMealSchedule(UUID mealScheduleId) {
        log.info("Checking if meal schedule with id {} exists.", mealScheduleId);
        return mealScheduleRepository.findById(mealScheduleId).isPresent();
    }

    public double countCarbs (List<Meal> meals) {
        double result = 0;
        for (Meal meal : meals) {
            result += meal.getCarbs();
        }
        return result;
    }
    public double countProteins (List<Meal> meals) {
        double result = 0;
        for (Meal meal : meals) {
            result += meal.getProteins();
        }
        return result;
    }
    public double countFats (List<Meal> meals) {
        double result = 0;
        for (Meal meal : meals) {
            result += meal.getFats();
        }
        return result;
    }
    public double countCalories (List<Meal> meals) {
        double result = 0;
        for (Meal meal : meals) {
            result += meal.getCalories();
        }
        return result;
    }

    public void setNutrition(MealSchedule mealSchedule, List<Meal> meals){
        meals.stream().forEach(meal -> {
            double calories = meal.getCalories();
            double proteins = meal.getProteins();
            double fats = meal.getFats();
            double carbs = meal.getCarbs();
            mealSchedule.setCalories(calories);
            mealSchedule.setProteins(proteins);
            mealSchedule.setFats(fats);
            mealSchedule.setCarbs(carbs);
        });
    }

    public void updateAllNutrition(){
        List<MealSchedule> mealSchedules = mealScheduleRepository.findAll();
        for (MealSchedule mealSchedule : mealSchedules) {
            List<Meal> meals = mealSchedule.getMeals();
            this.setNutrition(mealSchedule, meals);
        }
    }

    @EventListener
    public void handleIngredientUpdatedEvent(IngredientUpdatedEvent event) {
        this.updateAllNutrition();
    }
    @EventListener
    public void handleMealUpdatedEvent (MealUpdatedEvent event) {
        this.updateAllNutrition();
    }

}


