package com.example.cookit.repositories;

import com.example.cookit.entities.MealSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MealScheduleRepository extends JpaRepository<MealSchedule, UUID> {

}
