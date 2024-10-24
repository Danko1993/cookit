package com.example.cookit.services;

import com.example.cookit.DTO.HomeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Service
public class HomeService {
    @Autowired
    private MealService mealService;
    @Autowired
    private IngredientService ingredientService;


    public ResponseEntity<HomeDto> getHomeData() {
        HomeDto homeDto = new HomeDto();
        homeDto.setMealsNumber(mealService.countMeals());
        homeDto.setIngredientsNumber(ingredientService.getNumberOfIngredients());
        return new ResponseEntity<>(homeDto, HttpStatus.OK);
    }
}
