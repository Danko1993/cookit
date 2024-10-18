package com.example.cookit.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record RegisterDto(
        @NotBlank(message = "Username can not be empty")
        String username,
        @NotBlank(message = "Email can not be empty")
        @Email(message = "Invalid email format")
        String email,
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,10}$",
                message = "Password must be between 6 and 10 characters long and contain at least one uppercase letter, one digit, and one special character.")
        String password) {
}
