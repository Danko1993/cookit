package com.example.cookit.services;

import com.example.cookit.DTO.AppUserDto;
import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import com.example.cookit.mappers.AppUserMapper;
import com.example.cookit.repositories.ActivationTokenRepository;
import com.example.cookit.repositories.AppUserRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final AppUserMapper userMapper = AppUserMapper.INSTANCE;

    public String register(AppUserDto appUserDto){
        log.info("Próbuję zarejestrować użytkownika");
        log.info("Sprawdzam czy uzytkownik o takim adresie email istnieje");
        if (appUserRepository.findByEmail(appUserDto.email()) !=null){
            log.info("Uzytkownik o takim adresie email już istnieje");
            return "Użytkownik o podanym adresie e-mail już istnieje";
        }
        AppUser appUser = userMapper.toEntity(appUserDto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRoles("ROLE_USER");
        appUser.setEnabled(false);

        appUserRepository.save(appUser);
        log.info("użytkownik zapisany do bazy");

        return sendActivationToken(appUser.getEmail());
    }

    public void createActivationToken(AppUser appUser, String token){
        log.info("generuję token do aktywacji konta");
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setAppUser(appUser);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);
        activationToken.setExpiryDate(calendar.getTime());
        activationTokenRepository.save(activationToken);
        log.info("token gotowy");
    }

    public boolean activateAccount(String token){
        ActivationToken activationToken = activationTokenRepository.findActivationTokenByToken(token);
        if (activationToken !=null){
            log.info("Token istnieje");
            if (activationToken.getExpiryDate().after(new Date())){
                log.info("Token jest ważny");
                AppUser appUser = activationToken.getAppUser();
                appUser.setEnabled(true);
                appUserRepository.save(appUser);
                log.info("Konto aktywowane");
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public String sendActivationToken(String email){
        AppUser appUser = appUserRepository.findByEmail(email);
        log.info("Sprawdzam czy uzytkownik o takim adresie email istnieje aby wysłac link aktywacyjny");
        if (appUser == null){
            log.info("Uzytkownik o takim adresie email nie istnieje");
            return "Użytkownik z tym e-mailem nie istnieje.";
        }
        if (appUser.isEnabled()){
            log.info("To konto jest już aktywne");
            return "Konto jest już aktywne.";
        }
        String token = UUID.randomUUID().toString();
        createActivationToken(appUser,token);
        String activationLink = "http://localhost:8080/register/activate?token=" + token;
        try {
            emailService.sendActivationLink(appUser.getEmail(), activationLink);
        } catch (MessagingException e){
            log.info("Błąd wysyłki linku aktywacyjnego");
            e.printStackTrace();
            return "Błąd podczas wysyłania linku aktywacyjnego";
        }
        log.info("Email aktywacyjny wysłany");
        return "Rejestracja powiodła się, sprawdź swój e-mail, aby aktywować konto.";
    }
}
