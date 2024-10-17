package com.example.cookit.controllers;

import com.example.cookit.DTO.AppUserDto;
import com.example.cookit.services.EmailService;
import com.example.cookit.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private AppUserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping
    public String register(@RequestBody AppUserDto userDto){
        log.info("rozpoczynam rejestracje");
        return userService.register(userDto);
    }

    @GetMapping("/activate")
    public String activate(@RequestParam("token") String token){
        log.info("Rozpoczynam aktywację");
        if (userService.activateAccount(token)){
            return "Twoje konto zostało aktywowane";
        }else {
            return "Błąd aktywacji";
        }
    }
    @PostMapping("/resend-token")
    public String resendToken(@RequestParam("email")String email){
        return userService.sendActivationToken(email);
    }
}
