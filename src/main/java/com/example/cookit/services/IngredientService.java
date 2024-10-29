package com.example.cookit.services;

import com.example.cookit.DTO.IngredientDto;
import com.example.cookit.DTO.UpdateIngredientDto;
import com.example.cookit.entities.Ingredient;
import com.example.cookit.events.EventPublisher;
import com.example.cookit.mappers.IngredientMapper;
import com.example.cookit.repositories.IngredientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private EventPublisher eventPublisher;

    private final IngredientMapper ingredientMapper = IngredientMapper.INSTANCE;
    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public ResponseEntity<String> addIngredient(IngredientDto ingredientDto) {
        log.info("Adding ingredient " + ingredientDto.name() + " to the database");
        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
        ingredientRepository.save(ingredient);
        log.info("Ingredient " + ingredientDto.name() + " added to the database");
        return new ResponseEntity<>("Ingredient " + ingredientDto.name() + " added to the database",HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> addOrUpdateIngredientPhoto(String id, MultipartFile file) {
        UUID ingredientId = UUID.fromString(id);
        if (ingredientRepository.existsById(ingredientId)) {
            Ingredient ingredient = ingredientRepository.findById(ingredientId).get();
            if (!ingredient.getImagePath().isEmpty()){
                try {
                    fileStorageService.deleteFile(ingredient.getImagePath());
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
                }
                ingredient.setImagePath("");
            }
            String path;
            try {
                path=fileStorageService.saveFile(file,"ingredient");
            }catch (IOException e){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            ingredient.setImagePath(path);
            ingredientRepository.save(ingredient);
            return new ResponseEntity<>("Added ingredient photo.",HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Ingredient with id " + ingredientId + " not found",HttpStatus.NOT_FOUND);
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

    public boolean ingredientExist(UUID id) {
       return ingredientRepository.existsById(id);
    }

}
