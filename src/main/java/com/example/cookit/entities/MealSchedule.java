package com.example.cookit.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class MealSchedule {
    @Id
    private UUID id=UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "diet_plan_id", nullable = false)
    private DietPlan dietPlan;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "meal_schedule_meals",
            joinColumns = @JoinColumn(name = "meal_schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private List<Meal> meals;
}
