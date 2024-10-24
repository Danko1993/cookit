package com.example.cookit.repositories;

import com.example.cookit.entities.AppUser;
import com.example.cookit.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {
    Meal findMealById(UUID id);
    void deleteMealById(UUID id);
}
