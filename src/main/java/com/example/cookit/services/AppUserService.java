package com.example.cookit.services;

import com.example.cookit.DTO.RegisterDto;
import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import com.example.cookit.mappers.AppUserMapper;
import com.example.cookit.repositories.AppUserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
@Slf4j
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivationTokenService activationTokenService;


    private final AppUserMapper appUserMapper = AppUserMapper.INSTANCE;

    @Transactional
    public ResponseEntity<String> registerUser(RegisterDto registerDto) {
        log.info("Checking if email:{} is already taken", registerDto.email());
        if (appUserRepository.findByEmail(registerDto.email()) != null) {
            log.info("Email:{} is already taken", registerDto.email());
            return new ResponseEntity<>("Email: "
                    + registerDto.email() + " is already taken", HttpStatus.CONFLICT);
        }
        AppUser appUser = appUserMapper.toEntity(registerDto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRoles("ROLE_USER");
        appUser.setEnabled(false);

        appUserRepository.save(appUser);
        log.info("User: {} registered successfully", appUser.getUsername());

        return activationTokenService.sendActivationToken(appUser);
    }

    @Transactional
    public ResponseEntity<String> activateAccount(String token) {
        if (activationTokenService.validateActivationToken(token)) {
            ActivationToken activationToken = activationTokenService.getActivationTokenByToken(token);
            AppUser appUser = activationToken.getAppUser();
            if (appUser.isEnabled()) {
                log.info("User: {} is active", appUser.getUsername());
                return new ResponseEntity<>("Account is already active", HttpStatus.CONFLICT);
            }
            appUser.setEnabled(true);
            appUserRepository.save(appUser);
            log.info("Account:{} activated successfully",
                    activationTokenService.getActivationTokenByToken(token).getAppUser().getUsername());
            return new ResponseEntity<>("Activation successful", HttpStatus.OK);
        } else {
            log.info("Invalid activation token");
            return new ResponseEntity<>("Invalid activation token", HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<String> resendToken(String email) {
        AppUser appUser = appUserRepository.findByEmail(email);
        if (appUser != null) {
            log.info("Resending activation token for email: {}", email);
            activationTokenService.deleteActivationToken(appUser);
            return activationTokenService.sendActivationToken(appUserRepository.findByEmail(email));
        } else {
            log.info("Email: {} not found", email);
            return new ResponseEntity<>("Email: " + email + " not found", HttpStatus.NOT_FOUND);
        }
    }

    public boolean userExists(UUID id) {
        return appUserRepository.existsById(id);
    }

    public AppUser getUserById(UUID id) {
        return appUserRepository.findById(id).orElse(null);
    }


}
