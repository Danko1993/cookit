package com.example.cookit.repositories;

import com.example.cookit.entities.AppUser;
import com.example.cookit.entities.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, UUID> {

     DietPlan getDietPlanById(UUID id);
    void deleteById(UUID id);
    List<DietPlan> findDietPlansByAppUser(AppUser appUser);
}
