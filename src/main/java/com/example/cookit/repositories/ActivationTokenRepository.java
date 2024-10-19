package com.example.cookit.repositories;

import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivationTokenRepository extends JpaRepository <ActivationToken, UUID> {

    ActivationToken findActivationTokenByToken(String token);
    void deleteActivationTokenByAppUser(AppUser appUser);
}
