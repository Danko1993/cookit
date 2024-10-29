package com.example.cookit.services;

import com.example.cookit.DTO.MealScheduleDto;
import com.example.cookit.DTO.SendMealDto;
import com.example.cookit.DTO.SendMealScheduleDto;
import com.example.cookit.DTO.UpdateMealScheduleDto;
import com.example.cookit.entities.Meal;
import com.example.cookit.entities.MealSchedule;
import com.example.cookit.repositories.MealScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestMealScheduleService {
    @InjectMocks
    private MealScheduleService mealScheduleService;
    @Mock
    private MealScheduleRepository mealScheduleRepository;
    @Mock
    private DietPlanService dietPlanService;
    @Mock
    private MealService mealService;

    private MealSchedule mealSchedule;
    private MealSchedule mealSchedule2;
    private List<MealSchedule> mealScheduleList;
    private MealScheduleDto mealScheduleDto;
    private List<UUID> mealsId;
    private List<Meal> meals;
    private Meal meal;
    private Meal meal2;
    private UpdateMealScheduleDto updateMealScheduleDto;
    private SendMealDto sendMealDto;
    private SendMealDto sendMealDto2;
    private List<SendMealDto> sendMealDtoList;
    private SendMealScheduleDto sendMealScheduleDto;
    private SendMealScheduleDto sendMealScheduleDto2;
    private List<SendMealScheduleDto> sendMealScheduleDtoList;
    Map<UUID,Double> ingredientsWithWeight;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID mealId = UUID.randomUUID();
        UUID mealId2 = UUID.randomUUID();
        mealsId = new ArrayList<>();
        mealsId.add(mealId);
        mealsId.add(mealId2);
        meal = new Meal();
        meal.setId(mealId);
        meal.setName("test1");
        meal.setDescription("description1");
        meal.setProteins(30.0);
        meal.setFats(40.0);
        meal.setCarbs(50.0);
        meal.setCalories(600.0);
        meal2 = new Meal();
        meal2.setId(mealId2);
        meal2.setName("test2");
        meal2.setDescription("description2");
        meal2.setProteins(50.0);
        meal2.setFats(20.0);
        meal2.setCarbs(30.0);
        meal2.setCalories(400.0);
        mealScheduleDto = new MealScheduleDto(UUID.randomUUID(),new Date());
        meals = new ArrayList<>();
        meals.add(meal);
        meals.add(meal2);
        updateMealScheduleDto = new UpdateMealScheduleDto(UUID.randomUUID(),new Date(),mealsId);
        mealSchedule = new MealSchedule();
        mealSchedule.setMeals(meals);
        mealSchedule.setId(UUID.randomUUID());
        mealSchedule.setDate(new Date());
        mealSchedule.setDietPlanId(UUID.randomUUID());
        mealSchedule2 = new MealSchedule();
        mealSchedule2.setMeals(meals);
        mealSchedule2.setId(UUID.randomUUID());
        mealSchedule2.setDate(new Date());
        mealSchedule2.setDietPlanId(UUID.randomUUID());
        mealScheduleList = new ArrayList<>();
        mealScheduleList.add(mealSchedule);
        mealScheduleList.add(mealSchedule2);
        ingredientsWithWeight = new HashMap<>();
        ingredientsWithWeight.put(UUID.randomUUID(), 10.0);
        ingredientsWithWeight.put(UUID.randomUUID(), 20.0);
        sendMealDto = new SendMealDto
                (meal.getId(),"test1","description1",
                        ingredientsWithWeight,600.0,50.0,30.0,40.0);
        sendMealDto2 = new SendMealDto(meal2.getId(),"test2","description2",
                ingredientsWithWeight,400.0,30.0,50.0,20.0);
        sendMealDtoList = new LinkedList<>();
        sendMealDtoList.add(sendMealDto);
        sendMealDtoList.add(sendMealDto2);
        sendMealScheduleDto = new SendMealScheduleDto
                (UUID.randomUUID(),new Date(),sendMealDtoList,50.0,70.0,90.0,110.0);
        sendMealScheduleDto2 = new SendMealScheduleDto
                (UUID.randomUUID(),new Date(),sendMealDtoList,50.0,70.0,90.0,110.0);
        sendMealScheduleDtoList = new ArrayList<>();
        sendMealScheduleDtoList.add(sendMealScheduleDto);
        sendMealScheduleDtoList.add(sendMealScheduleDto2);

    }
    @Test
    void addMealScheduleAndBindWithDietPlanSuccess() {
        when(dietPlanService.existById(mealScheduleDto.dietPlanId())).thenReturn(true);
        when(mealService.getMealsById(mealsId)).thenReturn(meals);
        when(mealScheduleRepository.save(mealSchedule)).thenAnswer(invocation -> {
            MealSchedule mealSchedule = invocation.getArgument(0);
            mealSchedule.setId(mealSchedule.getId());
            return mealSchedule;
        });

        ResponseEntity<String> response = mealScheduleService.addMealScheduleAndBindWithDietPlan(mealScheduleDto);

        ArgumentCaptor<MealSchedule> captor = ArgumentCaptor.forClass(MealSchedule.class);

        verify(dietPlanService,times(1)).existById(mealScheduleDto.dietPlanId());
        verify(mealScheduleRepository).save(captor.capture());
        assertEquals(response.getBody(),"Meal schedule added successfully.");
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);

    }

    @Test
    void addMealScheduleAndBindWithDietPlanFailureDietPlanNotFound() {
        when(dietPlanService.existById(mealScheduleDto.dietPlanId())).thenReturn(false);

        ResponseEntity<String> response = mealScheduleService.addMealScheduleAndBindWithDietPlan(mealScheduleDto);

        verify(dietPlanService,times(1)).existById(mealScheduleDto.dietPlanId());
        verify(mealService,times(0)).getMealsById(mealsId);
        verify(mealScheduleRepository,times(0)).save(mealSchedule);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getBody(),"Diet plan with id" + mealScheduleDto.dietPlanId() + " not found.");
    }
    @Test
    void updateMealScheduleSuccess() {
        when(mealScheduleRepository.existsById(updateMealScheduleDto.mealScheduleId())).thenReturn(true);
        when(mealScheduleRepository.findById(updateMealScheduleDto.mealScheduleId())).thenReturn(Optional.of(mealSchedule));
        when(mealService.getMealsById(updateMealScheduleDto.mealIds())).thenReturn(meals);

        ResponseEntity<String> response = mealScheduleService.updateMealSchedule(updateMealScheduleDto);
        verify(mealScheduleRepository,times(1)).existsById(updateMealScheduleDto.mealScheduleId());
        verify(mealScheduleRepository,times(1)).findById(updateMealScheduleDto.mealScheduleId());
        verify(mealScheduleRepository,times(1)).save(mealSchedule);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(),"Meal schedule updated successfully.");
    }
    @Test
    void updateMealScheduleFailureMealScheduleNotFound() {
        when(mealScheduleRepository.existsById(updateMealScheduleDto.mealScheduleId())).thenReturn(false);

        ResponseEntity<String> response = mealScheduleService.updateMealSchedule(updateMealScheduleDto);

        verify(mealScheduleRepository,times(1)).existsById(updateMealScheduleDto.mealScheduleId());
        verify(mealScheduleRepository,times(0)).findById(updateMealScheduleDto.mealScheduleId());
        verify(mealService,times(0)).getMealsById(mealsId);
        verify(mealScheduleRepository,times(0)).save(mealSchedule);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getBody(),"Meal schedule with id: "
                + updateMealScheduleDto.mealScheduleId() + " not found.");
    }
    @Test
    void getMealScheduleByDietPlanIdSuccess() {
        when(mealScheduleRepository.findAll()).thenReturn(mealScheduleList);

        UUID uuid = UUID.randomUUID();
        ResponseEntity<List<SendMealScheduleDto>> response = mealScheduleService.getMealSchedulesByDietPlanId(uuid);
        verify(mealScheduleRepository,times(1)).findAll();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void checkMealScheduleTest(){
        UUID uuid = UUID.randomUUID();
        when(mealScheduleRepository.existsById(uuid)).thenReturn(true);
        boolean result = mealScheduleService.checkMealSchedule(uuid);
        verify(mealScheduleRepository,times(1)).existsById(uuid);
        assertEquals(result,true);
    }
    @Test
    void checkMealScheduleFailureTest(){
        UUID uuid = UUID.randomUUID();
        when(mealScheduleRepository.existsById(uuid)).thenReturn(false);
        boolean result = mealScheduleService.checkMealSchedule(uuid);
        verify(mealScheduleRepository,times(1)).existsById(uuid);
        assertEquals(result,false);
    }
    @Test
    void updateAllNutritionTest(){
        mealScheduleService.updateAllNutrition();
        verify(mealScheduleRepository,times(1)).findAll();
    }

}
