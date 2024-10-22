package com.example.cookit.repositories;

import com.example.cookit.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    Ingredient findByName(String name);
   default Ingredient findIngredientById(UUID id){
       return findById(id).orElse(null);
   };
    void deleteIngredientById(UUID id);
}
