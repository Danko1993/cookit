package com.example.cookit.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Meal {
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    @JsonBackReference
    private AppUser appUser;

    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(name = "meal_ingredient", joinColumns = @JoinColumn(name = "meal_id"))
    @MapKeyJoinColumn(name = "ingredient_id")
    private Map<Ingredient, Double> ingredientsWithWeight ;

    @JsonBackReference
    @ManyToMany(mappedBy = "meals", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealSchedule> mealSchedules;

    private double calories;

    private double carbs;

    private double proteins;

    private double fats;

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", calories=" + calories +
                ", carbs=" + carbs +
                ", proteins=" + proteins +
                ", fats=" + fats +
                '}';
    }

}
