package com.example.cookit.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class ShoppingList {

    @Id
    private UUID id = UUID.randomUUID();

    private String name;

    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    @JsonBackReference
    private AppUser appUser;

    @ElementCollection
    @CollectionTable(name = "shopping_list_items", joinColumns = @JoinColumn(name = "shopping_list_id"))
    @MapKeyColumn(name = "ingredient_name")
    @Column(name = "ingredient_weight")
    private Map<String, Double> ingredientsWithWeight = new HashMap<>();


    boolean isBought;
}
