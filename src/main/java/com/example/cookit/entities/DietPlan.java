package com.example.cookit.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class DietPlan {
    @Id
    private UUID id = UUID.randomUUID();

    private String name;

    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    private Double dailyCalories;

    @OneToMany(mappedBy = "dietPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealSchedule> mealSchedules;
}
