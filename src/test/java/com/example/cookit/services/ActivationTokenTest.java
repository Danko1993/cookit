package com.example.cookit.services;


import com.example.cookit.entities.ActivationToken;
import com.example.cookit.entities.AppUser;
import com.example.cookit.repositories.ActivationTokenRepository;
import jakarta.mail.MessagingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ActivationTokenTest {

    @InjectMocks
    private ActivationTokenService activationTokenService;

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendActivationToken_Success() throws MessagingException {
        AppUser appUser = new AppUser();
        appUser.setEmail("test@test.com");
        appUser.setUsername("test");

        doNothing().when(emailService).sendActivationLink(anyString(), anyString());

        ResponseEntity<String> response = activationTokenService.sendActivationToken(appUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Activation email for user " + appUser.getUsername() + " sent successfully", response.getBody());

        verify(emailService, times(1)).
                sendActivationLink(anyString(),any(String.class));
        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
    }

    @Test
    void testSendActivationToken_Failure() throws MessagingException {
        AppUser appUser = new AppUser();
        appUser.setEmail("test@test.com");
        appUser.setUsername("test");

        doThrow(new MessagingException("Failed to send email")).when(emailService).sendActivationLink(anyString(), anyString());

        ResponseEntity<String> response = activationTokenService.sendActivationToken(appUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Activation emial for user " + appUser.getUsername() +" could not be sent due to: Failed to send email", response.getBody());
        verify(emailService,times(1)).sendActivationLink(anyString(),any(String.class));
        verify(activationTokenRepository,times(1)).save(any(ActivationToken.class));
        verify(activationTokenRepository,times(1)).deleteActivationTokenByAppUser(any(AppUser.class));
    }
    @Test
    void testCreateActivationToken() {
        AppUser appUser = new AppUser();
        String token = "testToken";
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setAppUser(appUser);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);
        activationToken.setExpiryDate(calendar.getTime());
        activationTokenRepository.save(activationToken);

        verify(activationTokenRepository,times(1)).save(any(ActivationToken.class));
    }
    @Test
    void testValidateActivationTokenSuccess() {
        String token = "testToken";
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);
        activationToken.setExpiryDate(calendar.getTime());
        when(activationTokenRepository.findActivationTokenByToken(token)).thenReturn(activationToken);

        boolean result = activationTokenService.validateActivationToken(token);
        assertTrue(activationTokenService.isTokenValid(activationToken));
        assertEquals(true, result);

        verify(activationTokenRepository,times(1)).findActivationTokenByToken(token);
    }
    @Test
    void testValidateActivationTokenFailure() {
        String token = "testToken";
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);
        activationToken.setExpiryDate(calendar.getTime());

        when(activationTokenRepository.findActivationTokenByToken(token)).thenReturn(null);
        boolean result = activationTokenService.validateActivationToken(token);
        assertEquals(false, result);
    }
    @Test
    void testIsTokenValidSuccess() {
        ActivationToken activationToken = new ActivationToken();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);
        activationToken.setExpiryDate(calendar.getTime());
        boolean result = activationTokenService.isTokenValid(activationToken);
        assertEquals(true, result);
    }
    @Test
    void testIsTokenValidFailure() {
        ActivationToken activationToken = new ActivationToken();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -24);
        activationToken.setExpiryDate(calendar.getTime());
        boolean result = activationTokenService.isTokenValid(activationToken);
        assertEquals(false, result);
    }
    @Test
    void testDeleteActivationToken() {
        AppUser appUser = new AppUser();

        activationTokenRepository.deleteActivationTokenByAppUser(appUser);

        verify(activationTokenRepository,times(1)).deleteActivationTokenByAppUser(appUser);
    }
}
