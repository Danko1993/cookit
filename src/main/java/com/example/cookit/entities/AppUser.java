package com.example.cookit.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


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
}
