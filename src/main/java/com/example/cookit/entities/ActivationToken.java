package com.example.cookit.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class ActivationToken {
    @Id
    private UUID id = UUID.randomUUID();

    private String token;

    @OneToOne
    @JoinColumn(nullable = false)
    private AppUser appUser;

    private Date expiryDate;
}
