package com.example.cookit.services;

import com.example.cookit.DTO.IngredientDto;
import com.example.cookit.DTO.UpdateIngredientDto;
import com.example.cookit.entities.Ingredient;
import com.example.cookit.events.EventPublisher;
import com.example.cookit.repositories.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestIngredientService {
    @InjectMocks
    private IngredientService ingredientService;


    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    EventPublisher eventPublisher;

    private Ingredient ingredient;
    private Ingredient ingredient2;
    private IngredientDto ingredientDto;
    private IngredientDto ingredientDto2;
    private UpdateIngredientDto updateIngredientDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDto = new IngredientDto
                ("test1",100.0,5.0,20.0,15.0);
        ingredientDto2 = new IngredientDto
                ("test2",50.0,7.0,13.0,70.0);
        updateIngredientDto = new UpdateIngredientDto(UUID.randomUUID(),"testupdated",70.0,22.0,20.0,20.0);
        ingredient = new Ingredient();
        ingredient.setName("test1");
        ingredient.setCaloriesPer100g(100.0);
        ingredient.setCarbsPer100g(5.0);
        ingredient.setProteinPer100g(15.0);
        ingredient.setFatsPer100g(15.0);
        ingredient2 = new Ingredient();
        ingredient2.setName("test2");
        ingredient2.setCaloriesPer100g(50.0);
        ingredient2.setCarbsPer100g(7.0);
        ingredient2.setProteinPer100g(13.0);
        ingredient2.setFatsPer100g(70.0);


    }
    @Test
    void addIngredientTest() {
        UUID uuid = UUID.randomUUID();
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(invocation -> {
            Ingredient ingredient = invocation.getArgument(0);
            ingredient.setId(uuid);
            return ingredient;
        });
        ResponseEntity<String> response = ingredientService.addIngredient(ingredientDto);
        ArgumentCaptor<Ingredient> captor = ArgumentCaptor.forClass(Ingredient.class);

        verify(ingredientRepository,times(1) ).save(any(Ingredient.class));
        assertEquals(response.getBody(),"Ingredient " + ingredientDto.name() + " added to the database");
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }
    @Test
    void addIngredientsTest(){
        List<IngredientDto> ingredientDtos = new ArrayList<>();
        ingredientDtos.add(ingredientDto);
        ingredientDtos.add(ingredientDto2);
        ResponseEntity<String> response = ingredientService.addIngredients(ingredientDtos);

        assertEquals(response.getBody(),"Ingredients " + ingredientDtos.toString() + " added to the database");
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }
    @Test
    void deleteIngredientTestSuccess() {
        UUID uuid = UUID.randomUUID();
        when(ingredientRepository.existsById(uuid)).thenReturn(true);

        ResponseEntity<String> response = ingredientService.deleteIngredient(uuid);

        verify(ingredientRepository,times(1)).existsById(uuid);
        verify(ingredientRepository,times(1)).deleteIngredientById(uuid);
        assertEquals(response.getBody(),"Ingredient with id :" + uuid + " deleted from the database");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void deleteIngredientTestFailure() {
        UUID uuid = UUID.randomUUID();
        when(ingredientRepository.existsById(uuid)).thenReturn(false);

        ResponseEntity<String> response = ingredientService.deleteIngredient(uuid);
        verify(ingredientRepository,times(1)).existsById(uuid);
        verify(ingredientRepository,times(0)).deleteIngredientById(uuid);
        assertEquals(response.getBody(),"Ingredient with id: " + uuid + " not found");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }
    @Test
    void updateIngredientTestSuccess() {
        when(ingredientRepository.existsById(updateIngredientDto.id())).thenReturn(true);
        when(ingredientRepository.findIngredientById(updateIngredientDto.id())).thenReturn(ingredient);


        ResponseEntity<String> response = ingredientService.updateIngredient(updateIngredientDto);


        verify(ingredientRepository,times(1)).save(any(Ingredient.class));
        verify(ingredientRepository,times(1)).existsById(updateIngredientDto.id());
        verify(ingredientRepository,times(1)).findIngredientById(updateIngredientDto.id());
        verify(eventPublisher,times(1)).publishIngredientUpdated(any(Ingredient.class));
        assertEquals(response.getBody(),"Ingredient " + updateIngredientDto.name() + " updated");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void updateIngredientTestFailure() {
        when(ingredientRepository.existsById(updateIngredientDto.id())).thenReturn(false);

        ResponseEntity<String> response = ingredientService.updateIngredient(updateIngredientDto);
        verify(ingredientRepository,times(1)).existsById(updateIngredientDto.id());
        verify(ingredientRepository,times(0)).findIngredientById(updateIngredientDto.id());
        verify(eventPublisher,times(0)).publishIngredientUpdated(any(Ingredient.class));
        assertEquals(response.getBody(),"Ingredient " + updateIngredientDto.name() + " not found");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }
    @Test
    void getAllIngredientsTest() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);
        ingredients.add(ingredient2);
        when(ingredientRepository.findAll()).thenReturn(ingredients);

        ResponseEntity<List<Ingredient>> response = ingredientService.getAllIngredients();
        verify(ingredientRepository,times(1)).findAll();
        assertEquals(response.getBody(),ingredients);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void getIngredientByIdSuccess() {
        UUID uuid = UUID.randomUUID();
        when(ingredientRepository.findIngredientById(uuid)).thenReturn(ingredient);
        ResponseEntity<Ingredient> response = ingredientService.getIngredientById(uuid);
        verify(ingredientRepository,times(1)).findIngredientById(uuid);
        assertEquals(response.getBody(),ingredient);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void getIngredientByIdFailure() {
        UUID uuid = UUID.randomUUID();
        when(ingredientRepository.findIngredientById(uuid)).thenReturn(null);
        ResponseEntity<Ingredient> response = ingredientService.getIngredientById(uuid);
        verify(ingredientRepository,times(1)).findIngredientById(uuid);
        assertEquals(response.getBody(),null);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }
    @Test
    void getNumberOfIngredientsSuccess() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);
        ingredients.add(ingredient2);
        when(ingredientRepository.findAll()).thenReturn(ingredients);

        Integer numberOfIngredients = ingredientService.getNumberOfIngredients();
        verify(ingredientRepository,times(1)).findAll();
        assertEquals(numberOfIngredients,ingredients.size());
    }
}
