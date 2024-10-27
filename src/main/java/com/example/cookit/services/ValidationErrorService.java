package com.example.cookit.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;
@Slf4j
@Service
public class ValidationErrorService {

    public ResponseEntity<String> returnValidationErrors(BindingResult result) {
        String validationErrors = result.getAllErrors().stream()
                .map(objectError -> {
                    log.warn(objectError.getDefaultMessage());
                    return objectError.getDefaultMessage();
                }).collect(Collectors.joining(", "));
        return new ResponseEntity<>("Validating erros: " + validationErrors, HttpStatus.BAD_REQUEST);
    }
}
