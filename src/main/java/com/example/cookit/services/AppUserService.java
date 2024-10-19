package com.example.cookit.services;

import com.example.cookit.DTO.RegisterDto;
import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import com.example.cookit.mappers.AppUserMapper;
import com.example.cookit.repositories.ActivationTokenRepository;
import com.example.cookit.repositories.AppUserRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivationTokenService activationTokenService;


    private final AppUserMapper appUserMapper = AppUserMapper.INSTANCE;


    public ResponseEntity<String> registerUser(RegisterDto registerDto){
        log.info("Checking if email:{} is already taken", registerDto.email());
        if (appUserRepository.findByEmail(registerDto.email()) !=null){
            log.info("Email:{} is already taken", registerDto.email());
            return new ResponseEntity<>("Email: "
                    +registerDto.email()+" is already taken", HttpStatus.CONFLICT);
        }
        AppUser appUser = appUserMapper.toEntity(registerDto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRoles("ROLE_USER");
        appUser.setEnabled(false);

        appUserRepository.save(appUser);
        log.info("User: {} registered successfully", appUser.getUsername());

        return activationTokenService.sendActivationToken(appUser);
    }


    public ResponseEntity<String> activateAccount(String token){
       if (activationTokenService.validateActivationToken(token)){
           ActivationToken activationToken = activationTokenRepository.findActivationTokenByToken(token);
           AppUser appUser = activationToken.getAppUser();
           appUser.setEnabled(true);
           appUserRepository.save(appUser);
           log.info("Account:{} activated successfully",
                   activationTokenRepository.findActivationTokenByToken(token).getAppUser().getUsername());
           return new ResponseEntity<>("Activation successful", HttpStatus.OK);
       }else {
           log.info("Invalid activation token");
           return new ResponseEntity<>("Invalid activation token", HttpStatus.CONFLICT);
       }
    }

    public ResponseEntity<String> resendToken(String email){
        if (appUserRepository.findByEmail(email) != null){
            log.info("Resending activation token for email: {}", email);
            activationTokenService.deleteActivationToken(appUserRepository.findByEmail(email));
            return activationTokenService.sendActivationToken(appUserRepository.findByEmail(email));
        }else {
            log.info("Email: {} not found", email);
            return new ResponseEntity<>("Email: "+email+" not found", HttpStatus.NOT_FOUND);
        }
    }




}
