package com.example.cookit.services;

import com.example.cookit.DTO.MealDto;
import com.example.cookit.DTO.MealScheduleDto;
import com.example.cookit.DTO.SendMealScheduleDto;
import com.example.cookit.DTO.UpdateMealScheduleDto;
import com.example.cookit.entities.Meal;
import com.example.cookit.entities.MealSchedule;
import com.example.cookit.mappers.MealMapper;
import com.example.cookit.mappers.MealScheduleMapper;
import com.example.cookit.repositories.DietPlanRepository;
import com.example.cookit.repositories.MealRepository;
import com.example.cookit.repositories.MealScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private DietPlanRepository dietPlanRepository;

    private final MealScheduleMapper mealScheduleMapper = MealScheduleMapper.INSTANCE;
    private final MealMapper mealMapper = MealMapper.INSTANCE;
    @Autowired
    private MealScheduleRepository mealScheduleRepository;
    @Autowired
    private MealRepository mealRepository;


    @Transactional
    public ResponseEntity<String> addMealScheduleAndBindWithDietPlan(MealScheduleDto mealScheduleDto) {
        log.info("Checking id diet plan with id {} exists.", mealScheduleDto.dietPlanId());
        if (dietPlanRepository.findById(mealScheduleDto.dietPlanId()).isPresent()) {
            log.info("Diet plan with id {} found.", mealScheduleDto.dietPlanId());
            boolean checkMeals = this.validateMealsIds(mealScheduleDto.mealsIds());
            if (checkMeals) {
                MealSchedule mealSchedule = mealScheduleMapper.toEntity(mealScheduleDto);
                List<Meal> meals = new ArrayList<>();
                for (UUID mealId : mealScheduleDto.mealsIds()) {
                    Meal meal = mealRepository.findById(mealId).orElse(null);
                    meals.add(meal);
                }
                mealSchedule.setMeals(meals);
                mealScheduleRepository.save(mealSchedule);
                log.info("Meal schedule added successfully.");
                return new ResponseEntity<>("Meal schedule added successfully.", HttpStatus.CREATED);
            }
        } else {
            log.warn("At least one meal not found in database.");
            return new ResponseEntity<>("At least one meal not found in database.", HttpStatus.NOT_FOUND);
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
            boolean checkMeals = this.validateMealsIds(updateMealScheduleDto.mealIds());
            if (checkMeals) {
                List<Meal> meals = new ArrayList<>();
                for (UUID mealId : updateMealScheduleDto.mealIds()) {
                    Meal meal = mealRepository.findById(mealId).orElse(null);
                    meals.add(meal);
                }
                existingMealSchedule.setMeals(meals);
            } else {
                log.warn("At least one meal not found in database.");
                return new ResponseEntity<>("At least one meal not found in database.", HttpStatus.NOT_FOUND);
            }
            log.info("Meal schedule updated successfully.");
            mealScheduleRepository.save(existingMealSchedule);
            return new ResponseEntity<>("Meal schedule updated successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Meal schedule with id: " + updateMealScheduleDto.mealScheduleId() + " not found.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<SendMealScheduleDto>> getMealSchedulesByDietPlanId(UUID id) {
        log.info("Checking id diet plan with id {} exists.", id);
        if (dietPlanRepository.findById(id).isPresent()) {
            log.info("Diet plan with id {} found.", id);
            List<MealSchedule> mealSchedules = dietPlanRepository.findById(id).get().getMealSchedules();
            List<SendMealScheduleDto> result = new ArrayList<>();
            for (MealSchedule mealSchedule : mealSchedules) {
                SendMealScheduleDto sendMealScheduleDto = mealScheduleMapper.toSendMealScheduleDto(mealSchedule);
                List<Meal> meals = mealSchedule.getMeals();
                List<MealDto> mealDtoList = new ArrayList<>();
                for (Meal meal : meals) {
                    mealMapper.toDto(meal);
                    mealDtoList.add(mealMapper.toDto(meal));
                    sendMealScheduleDto.setMeals(mealDtoList);
                }
                result.add(sendMealScheduleDto);
            }
            log.info("Meal schedules to be sent {}.", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        log.warn("Diet plan with id {} not found.", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    public boolean validateMealsIds(List<UUID> mealsIds) {

        for (UUID mealId : mealsIds) {
            if (!mealRepository.findById(mealId).isPresent()) {
                return false;
            }
        }
        return true;
    }

}


