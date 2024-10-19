package com.example.cookit.services;


import com.example.cookit.DTO.RegisterDto;
import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import com.example.cookit.mappers.AppUserMapper;
import com.example.cookit.repositories.ActivationTokenRepository;
import com.example.cookit.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AppUserServiceTest {

    @InjectMocks
    private AppUserService appUserService;

    private AppUser appUser;
    private ActivationToken activationToken;
    private RegisterDto registerDto;
    private String token;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ActivationTokenService activationTokenService;

    @Mock
    private  AppUserMapper appUserMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerDto = new RegisterDto("testUsername","testEmail","testPassword");


        appUser = new AppUser();
        appUser.setUsername("testUsername");
        appUser.setEmail("testEmail");
        appUser.setPassword("encodedPassword");
        appUser.setRoles("ROLE_USER");
        appUser.setEnabled(false);

        token ="testToken";
        activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setAppUser(appUser);

    }

    @Test
    void testRegisterUserSuccess () {
        UUID fixedUUID = UUID.randomUUID();
        appUser.setId(fixedUUID);
        when(appUserRepository.findByEmail(registerDto.email())).thenReturn(null);
        when(passwordEncoder.encode(registerDto.password())).thenReturn("encodedPassword");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(fixedUUID);
            return user;
        });
        when(activationTokenService.sendActivationToken(any(AppUser.class))).
                thenReturn(new ResponseEntity<String>("Activation email for user "+appUser.getUsername()+" sent successfully", HttpStatus.OK));

        ResponseEntity<String> response = appUserService.registerUser(registerDto);

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(activationTokenService).sendActivationToken(userCaptor.capture());

        assertEquals(fixedUUID, userCaptor.getValue().getId());

        verify(appUserRepository,times(1)).findByEmail(registerDto.email());
        verify(passwordEncoder,times(1)).encode(registerDto.password());
        verify(appUserRepository,times(1)).save(any(AppUser.class));
        verify(activationTokenService,times(1)).sendActivationToken(any(AppUser.class));
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Activation email for user "+appUser.getUsername()+" sent successfully",response.getBody());
    }

    @Test
    void testRegisterUserFailureEmailExists() {
        UUID fixedUUID = UUID.randomUUID();
        appUser.setId(fixedUUID);
        when(appUserRepository.findByEmail(registerDto.email())).thenReturn(appUser);

        ResponseEntity<String> response = appUserService.registerUser(registerDto);

        verify(appUserRepository,times(1)).findByEmail(registerDto.email());
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        assertEquals("Email: "
                +registerDto.email()+" is already taken",response.getBody());

    }

    @Test
    void testActivateAccountSuccess() {
        when(activationTokenService.validateActivationToken(token)).thenReturn(true);
        when(activationTokenRepository.findActivationTokenByToken(token)).thenReturn(activationToken);
        when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);

        ResponseEntity<String> response = appUserService.activateAccount(token);

        verify(activationTokenRepository,times(2)).findActivationTokenByToken(token);
        verify(activationTokenService,times(1)).validateActivationToken(token);
        verify(appUserRepository,times(1)).save(any(AppUser.class));
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Activation successful",response.getBody());

    }

    @Test
    void testActivateAccountFailureInvalidToken() {
        when(activationTokenService.validateActivationToken(token)).thenReturn(false);

        ResponseEntity<String> response = appUserService.activateAccount(token);

        verify(activationTokenService,times(1)).validateActivationToken(token);
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        assertEquals("Invalid activation token",response.getBody());
    }

    @Test
    void testResendTokenSuccess(){
        String email = "testEmail";
        when(appUserRepository.findByEmail(email)).thenReturn(appUser);
        when(activationTokenService.sendActivationToken(appUser)).thenReturn(new ResponseEntity<>
                ("Activation email for user "+appUser.getUsername()+" sent successfully", HttpStatus.OK));

        ResponseEntity<String> response = appUserService.resendToken(email);

        verify(appUserRepository,times(3)).findByEmail(email);
        verify(activationTokenService,times(1)).sendActivationToken(appUser);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Activation email for user "+appUser.getUsername()+" sent successfully",response.getBody());
    }

    @Test
    void testResendTokenFailureEmailDoesNotExist() {
        String email = "nonExistEmail";
        when(appUserRepository.findByEmail(email)).thenReturn(null);

        ResponseEntity<String> response = appUserService.resendToken(email);

        verify(appUserRepository,times(1)).findByEmail(email);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        assertEquals("Email: "+email+" not found",response.getBody());
    }

}
