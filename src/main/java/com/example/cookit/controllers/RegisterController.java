package com.example.cookit.controllers;

import com.example.cookit.DTO.RegisterDto;
import com.example.cookit.services.AppUserService;
import com.example.cookit.services.ValidationErrorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private ValidationErrorService validationErrorService;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDto registerDto, BindingResult result){
        log.info("Starting registration for user : {}", registerDto.email());

        if (result!=null && result.hasErrors()) {
            return validationErrorService.returnValidationErrors(result);
        }
        return appUserService.registerUser(registerDto);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam("token") String token){
        log.info("Starting activation for account with token:{}", token);
        if(token == null || token.isEmpty()){
            log.warn("Activation token can not be empty");
            return ResponseEntity.badRequest().body("Activation token can not be empty");
        }
        return appUserService.activateAccount(token);


    }
    @PostMapping("/resend_token")
    public ResponseEntity<String> resendToken(@RequestParam("email")String email){
        if (email == null || email.isEmpty()){
            return ResponseEntity.badRequest().body("Email can not be empty");
        }
        return appUserService.resendToken(email);

    }
}
