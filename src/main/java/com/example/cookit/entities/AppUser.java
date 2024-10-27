package com.example.cookit.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"meals", "shoppingLists"})
public class AppUser {

    @Id
    private UUID id = UUID.randomUUID();

    private String username;

    private String email;

    private String password;

    private String roles;

    private boolean enabled;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ShoppingList> shoppingLists = new ArrayList<>();

}
