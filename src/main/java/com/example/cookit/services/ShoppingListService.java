package com.example.cookit.services;

import com.example.cookit.DTO.MealScheduleDto;
import com.example.cookit.DTO.ReadyShopingListDto;
import com.example.cookit.DTO.ShoppingListDto;
import com.example.cookit.entities.Meal;
import com.example.cookit.entities.MealSchedule;
import com.example.cookit.entities.ShoppingList;
import com.example.cookit.mappers.ShoppingListMapper;
import com.example.cookit.repositories.ShoppingListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShoppingListService {

    private final ShoppingListMapper shoppingListMapper = ShoppingListMapper.INSTANCE;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private MealScheduleService mealScheduleService;

    @Autowired
    private AppUserService appUserService;

    @Transactional
    public ResponseEntity<String> addShoppingListAndBindWithUser(ShoppingListDto shoppingListDto) {
        log.info("Checking if user with id {} exists.", shoppingListDto.appUserId());
        if (appUserService.userExists(shoppingListDto.appUserId())) {
            log.info("User with id {} found.", shoppingListDto.appUserId());
            ShoppingList shoppingList = shoppingListMapper.toEntity(shoppingListDto);
            log.info("Getting meal schedules to prepare shopping list");
            shoppingList.setBought(false);
            shoppingListRepository.save(shoppingList);
            log.info("Added shoppingList to database.");
            return new ResponseEntity<>("Added shopping list to database", HttpStatus.CREATED);
        }
        log.warn("User with id {} not found.", shoppingListDto.appUserId());
        return new ResponseEntity<>("User with id : " + shoppingListDto.appUserId() + " not found.", HttpStatus.NOT_FOUND);
    }
    @Transactional
    public ResponseEntity<String> addIngredientsFromMealSchedules(List<UUID> mealSchedulesId, UUID shoppingListId) {
        log.info("Checking if meal schedules exists.");
        if (mealScheduleService.checkMealSchedules(mealSchedulesId)){
            if (shoppingListRepository.existsById(shoppingListId)){
                ShoppingList shoppingList = shoppingListRepository.getById(shoppingListId);
                shoppingList.setIngredientsWithWeight(this.prepareShoppingList(mealSchedulesId));
                shoppingListRepository.save(shoppingList);
                return new ResponseEntity<>("Ingreidents form meal schedules added to shopping list.", HttpStatus.OK);
            }
            return new ResponseEntity<>("Shopping list not found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Meal schedules not found.", HttpStatus.NOT_FOUND);
    }

    public Map<String, Double> prepareShoppingList(List<UUID> scheduleIds) {
        List<MealSchedule> mealSchedules = mealScheduleService.getMealSchedulesById(scheduleIds);
        if (mealSchedules == null || mealSchedules.isEmpty()) {
            log.warn("At least one MealSchedule not found.");
            return Collections.emptyMap();
        }
        List<Meal> meals = mealSchedules.stream().
                flatMap(mealSchedule -> mealSchedule.getMeals().stream()).collect(Collectors.toList());
        Map<String, Double> shoppingList = meals.stream().flatMap(meal -> meal.
                        getIngredientsWithWeight().entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Map.Entry::getValue,
                        Double::sum
                ));

        return shoppingList;
    }

    public ResponseEntity<List<ReadyShopingListDto>> getReadyShoppingListsByUser(UUID appUserId) {
        if (!appUserService.userExists(appUserId)) {
            log.warn("User with id {} not found.", appUserId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ShoppingList> shoppingLists = appUserService.getUserById(appUserId).getShoppingLists();
        if (shoppingLists == null || shoppingLists.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ReadyShopingListDto> readyShopingListDtos = new ArrayList<>();
        shoppingLists.forEach(shoppingList -> {
            ReadyShopingListDto readyShopingListDto = shoppingListMapper.toReadyShopingListDto(shoppingList);
            Map<String,Double> result = shoppingList.getIngredientsWithWeight().
                    entrySet().stream().collect(Collectors.toMap(
                    entry -> entry.getKey(),
                    Map.Entry::getValue
            ));
            readyShopingListDto.setIngredientsWithWeight(result);
            readyShopingListDtos.add(readyShopingListDto);
        });
        return  new ResponseEntity<>(readyShopingListDtos, HttpStatus.OK);
    }




}