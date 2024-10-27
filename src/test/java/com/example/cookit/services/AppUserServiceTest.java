package com.example.cookit.services;


import com.example.cookit.DTO.RegisterDto;
import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
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


import java.util.Optional;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    private ActivationTokenService activationTokenService;


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
        when(activationTokenService.getActivationTokenByToken(token)).thenReturn(activationToken);
        when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);

        ResponseEntity<String> response = appUserService.activateAccount(token);

        verify(activationTokenService,times(2)).getActivationTokenByToken(token);
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
    void testActivateAccountAccountActive(){
        appUser.setEnabled(true);
        when(activationTokenService.validateActivationToken(token)).thenReturn(true);
        when(activationTokenService.getActivationTokenByToken(token)).thenReturn(activationToken);
        ResponseEntity<String> response = appUserService.activateAccount(token);

        verify(activationTokenService,times(1)).validateActivationToken(token);
        verify(activationTokenService,times(1)).getActivationTokenByToken(token);
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
        assertEquals("Account is already active",response.getBody());
    }

    @Test
    void testResendTokenSuccess(){
        String email = "testEmail";
        when(appUserRepository.findByEmail(email)).thenReturn(appUser);
        when(activationTokenService.sendActivationToken(appUser)).thenReturn(new ResponseEntity<>
                ("Activation email for user "+appUser.getUsername()+" sent successfully", HttpStatus.OK));

        ResponseEntity<String> response = appUserService.resendToken(email);

        verify(appUserRepository,times(2)).findByEmail(email);
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
    @Test
    void testUserExistSuccess(){
        UUID uuid = UUID.randomUUID();
        appUser.setId(uuid);
        when(appUserRepository.existsById(uuid)).thenReturn(true);

        boolean exists = appUserService.userExists(uuid);

        verify(appUserRepository,times(1)).existsById(uuid);
        assertEquals(exists,true);

    }
    @Test
    void testUserExistFailure(){
        UUID uuid = UUID.randomUUID();
        appUser.setId(uuid);
        when(appUserRepository.existsById(uuid)).thenReturn(false);

        boolean exists = appUserService.userExists(uuid);

        verify(appUserRepository,times(1)).existsById(uuid);
        assertEquals(exists,false);
    }
    @Test
    void testUserExistIdNull(){
        UUID uuid = null;
        appUser.setId(uuid);
        when(appUserRepository.existsById(uuid)).thenThrow(new IllegalArgumentException("Id can must be provided."));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->appUserService.userExists(uuid));
        assertEquals(exception.getMessage(),"Id can must be provided.");

    }
    @Test
    void testGetUserByIdSuccess(){
        UUID uuid = UUID.randomUUID();
        appUser.setId(uuid);
        when(appUserRepository.findById(uuid)).thenReturn(Optional.of(appUser));

        AppUser result = appUserService.getUserById(uuid);
        verify(appUserRepository,times(1)).findById(uuid);
        assertNotNull(result);
        assertEquals(result,appUser);

    }

    @Test
    void testGetUserByIdFailure(){
        UUID uuid = UUID.randomUUID();
        appUser.setId(uuid);
        when(appUserRepository.findById(uuid)).thenReturn(Optional.empty());
        AppUser result = appUserService.getUserById(uuid);
        verify(appUserRepository,times(1)).findById(uuid);
        assertNull(result);
    }

    @Test
    void testGetUserByIdNull (){
        UUID uuid = null;
        appUser.setId(uuid);
        when(appUserRepository.findById(uuid)).thenThrow(new IllegalArgumentException("Id can must be provided."));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->appUserService.getUserById(uuid));
        assertEquals(exception.getMessage(),"Id can must be provided.");
    }
}
