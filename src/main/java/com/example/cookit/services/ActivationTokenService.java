package com.example.cookit.services;

import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import com.example.cookit.repositories.ActivationTokenRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
@Slf4j
@Service
public class ActivationTokenService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    public ResponseEntity<String> sendActivationToken(AppUser appUser){
        String token = UUID.randomUUID().toString();
        createActivationToken(appUser,token);
        String activationLink = "http://localhost:8080/register/activate?token=" + token;
        try {
            emailService.sendActivationLink(appUser.getEmail(), activationLink);
        } catch (MessagingException e){
            String errors = e.getMessage();
            log.info("Activation token for user {} could not be sent due to: {}",appUser.getUsername(), errors);
            this.deleteActivationToken(appUser);
            return new ResponseEntity<>("Activation emial for user "
                    +appUser.getUsername()+
                    " could not be sent due to: "+errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Activation email for user {} sent successfully",appUser.getUsername());
        return new ResponseEntity<>("Activation email for user "
                +appUser.getUsername()+" sent successfully",HttpStatus.OK);
    }

    public void createActivationToken(AppUser appUser, String token){
            ActivationToken activationToken = new ActivationToken();
            activationToken.setToken(token);
            activationToken.setAppUser(appUser);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR, 24);
            activationToken.setExpiryDate(calendar.getTime());
            activationTokenRepository.save(activationToken);
            log.info("Token saved to database");
    }

    public boolean validateActivationToken(String token){
        ActivationToken activationToken = activationTokenRepository.findActivationTokenByToken(token);
        if (activationToken != null){
            return isTokenValid(activationToken);
        }else {
            return false;
        }

    }

    public boolean isTokenValid(ActivationToken activationToken){
        Date currentDate = new Date();
        return currentDate.before(activationToken.getExpiryDate());
    }

    public void deleteActivationToken(AppUser appUser){
        activationTokenRepository.deleteActivationTokenByAppUser(appUser);
    }
}
