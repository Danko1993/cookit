package com.example.cookit.services;

import com.example.cookit.DTO.IngredientDto;
import com.example.cookit.DTO.UpdateIngredientDto;
import com.example.cookit.entities.Ingredient;
import com.example.cookit.events.EventPublisher;
import com.example.cookit.events.IngredientUpdatedEvent;
import com.example.cookit.mappers.IngredientMapper;
import com.example.cookit.repositories.IngredientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private EventPublisher eventPublisher;

    private final IngredientMapper ingredientMapper = IngredientMapper.INSTANCE;

    @Transactional
    public ResponseEntity<String> addIngredient(IngredientDto ingredientDto) {
        log.info("Adding ingredient " + ingredientDto.name() + " to the database");
        ingredientRepository.save(ingredientMapper.toEntity(ingredientDto));
        log.info("Ingredient " + ingredientDto.name() + " added to the database");
        return new ResponseEntity<>("Ingredient " + ingredientDto.name() + " added to the database",HttpStatus.CREATED);
    }
    @Transactional
    public ResponseEntity<String> addIngredients(List<IngredientDto> ingredientDtos) {
        log.info("Adding ingredients " + ingredientDtos.toString() + " ingredients to the database");
        for (IngredientDto ingredientDto : ingredientDtos) {
            this.addIngredient(ingredientDto);
            log.info("Ingredient " + ingredientDto.name() + " added to the database");
        }
        log.info("Ingredients added to the database");
        return new ResponseEntity<>("Ingredients " + ingredientDtos.toString() + " added to the database",HttpStatus.CREATED);
    }
    @Transactional
    public ResponseEntity<String> deleteIngredient(UUID id) {
        if (ingredientRepository.existsById(id)) {
            log.info("Deleting ingredient with id :" + id + " from the database");
            ingredientRepository.deleteIngredientById(id);
            log.info("Ingredient with id :" + id + " deleted from the database");
            return new  ResponseEntity<>("Ingredient with id :" + id + " deleted from the database", HttpStatus.OK);
        }
        log.warn("Ingredient " + id + " not found");
        return new ResponseEntity<>("Ingredient with id: " + id + " not found",HttpStatus.NOT_FOUND);

    }
    @Transactional
    public ResponseEntity<String> updateIngredient(UpdateIngredientDto updateIngredientDto) {
        if (ingredientRepository.existsById(updateIngredientDto.id())) {
            log.info("Updating ingredient " + updateIngredientDto.name() + " to the database");
            Ingredient existingIngredient = ingredientRepository.findIngredientById(updateIngredientDto.id());
            ingredientMapper.updateIngredient(updateIngredientDto, existingIngredient);
            ingredientRepository.save(existingIngredient);
            eventPublisher.publishIngredientUpdated(existingIngredient);
            log.info("Ingredient with name " + updateIngredientDto.name() + " updated");
            return new ResponseEntity<>("Ingredient " + updateIngredientDto.name() + " updated", HttpStatus.OK);
        }
            log.warn("Ingredient with name " + updateIngredientDto.name() + " not found");
            return new ResponseEntity<>("Ingredient " + updateIngredientDto.name() + " not found", HttpStatus.NOT_FOUND);

    }

    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return new ResponseEntity<List<Ingredient>>(ingredients,HttpStatus.OK);
    }

    public ResponseEntity<Ingredient> getIngredientById(UUID id) {
        Ingredient ingredient = ingredientRepository.findIngredientById(id);
        if (ingredient == null) {
            log.warn("Ingredient " + id + " not found");
            return new ResponseEntity<>(ingredient,HttpStatus.NOT_FOUND);
        }else {
            log.info("Ingredient " + id + " found");
            return new ResponseEntity<>(ingredient,HttpStatus.OK);
        }
    }

    public Ingredient getById(UUID id) {
        return ingredientRepository.findIngredientById(id);
    }

    public Integer getNumberOfIngredients() {
        return ingredientRepository.findAll().size();
    }

    public boolean ingredientsExist(List<UUID> ids) {
       return ids.stream().allMatch(id -> ingredientRepository.existsById(id));
    }

}
