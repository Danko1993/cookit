package com.example.cookit.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Ingredient {
    @Id
    private UUID id=UUID.randomUUID();

    private String name;

    private double caloriesPer100g;

    private double carbsPer100g;

    private double proteinPer100g;

    private double fatsPer100g;

}
