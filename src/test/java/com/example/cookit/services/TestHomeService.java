package com.example.cookit.services;

import com.example.cookit.DTO.HomeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestHomeService {
    @InjectMocks
    private HomeService homeService;
    @Mock
    private MealService mealService;
    @Mock
    private IngredientService ingredientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetHomeData() {
        when(mealService.countMeals()).thenReturn(2);
        when(ingredientService.getNumberOfIngredients()).thenReturn(2);

        ResponseEntity<HomeDto> home = homeService.getHomeData();
        verify(ingredientService,times(1)).getNumberOfIngredients();
        verify(mealService,times(1)).countMeals();
        assertEquals(home.getBody(),new HomeDto(2,2));
    }
}
