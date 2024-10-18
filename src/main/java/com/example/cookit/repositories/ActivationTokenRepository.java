package com.example.cookit.repositories;

import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivationTokenRepository extends JpaRepository <ActivationToken, UUID> {

    ActivationToken findActivationTokenByToken(String token);
    void deleteActivationTokenByAppUser(AppUser appUser);
}
