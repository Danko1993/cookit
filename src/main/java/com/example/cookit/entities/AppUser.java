package com.example.cookit.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class AppUser {

    @Id
    private UUID id = UUID.randomUUID();

    private String username;

    private String email;

    private String password;

    private String roles;

    private boolean enabled;

    @OneToMany(mappedBy = "appUser")
    private List<Meal> meals;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShoppingList> shoppingLists = new ArrayList<>();

}
