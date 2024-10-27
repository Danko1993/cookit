package com.example.cookit.repositories;

import com.example.cookit.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository <AppUser, UUID> {
    AppUser findAppUserById(UUID id);
   Optional <AppUser> findByUsername(String username);
    AppUser findByEmail(String email);
}
