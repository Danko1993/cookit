package com.example.cookit.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private AppUser appUser;

    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(name = "meal_ingredient", joinColumns = @JoinColumn(name = "meal_id"))
    @MapKeyJoinColumn(name = "ingredient_id")
    private Map<Ingredient, Double> ingredientsWithWeight ;


}
