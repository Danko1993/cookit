package com.example.cookit.controllers;

import com.example.cookit.DTO.AuthRequestDto;
import com.example.cookit.entities.AppUser;
import com.example.cookit.repositories.AppUserRepository;
import com.example.cookit.services.AppUserDetailsService;
import com.example.cookit.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppUserRepository appUserRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid AuthRequestDto authRequestDto, BindingResult result) {
        log.info("Received authentication request");

        try {
            log.info("Starting authentication for username: {}", authRequestDto.username());
            Optional<AppUser> appUserOptional = appUserRepository.findByUsername(authRequestDto.username());

            if (result.hasErrors()) {
                log.warn("Validation errors: {}", result.getAllErrors());
                return ResponseEntity.badRequest()
                        .body("Validation errors: " + result.getAllErrors().get(0).getDefaultMessage());
            }

            AppUser appUser = appUserOptional.get();
            if (!appUser.isEnabled()) {
                log.error("User {} is not activated", appUser.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is not activated.");
            }

            log.info("Attempting authentication for user: {}", authRequestDto.username());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.username(), authRequestDto.password())
            );

            log.info("Authentication successful for username: {}", appUser.getUsername());
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(appUser.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails.getUsername());
            log.info("Generated JWT for username {}: {}", userDetails.getUsername(), jwt);
            return ResponseEntity.ok(jwt);

        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password.");
        }
    }
}