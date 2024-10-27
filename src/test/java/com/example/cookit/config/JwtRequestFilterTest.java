package com.example.cookit.config;

import com.example.cookit.util.JwtUtil;
import jakarta.servlet.ServletException;
import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Incubating;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class JwtRequestFilterTest {

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    UserDetails userDetails;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    Authorization authorization;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidToken() throws IOException, ServletException {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtil.extractUsername("validToken")).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);
        when(jwtUtil.validateToken("validToken", userDetails.getUsername())).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request,response,chain);

        verify(userDetailsService,times(1)).loadUserByUsername("username");
        verify(chain,times(1)).doFilter(request,response);
        assert SecurityContextHolder.getContext().getAuthentication() != null;
    }

    @Test
    void testInvalidToken() throws IOException, ServletException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtUtil.extractUsername("invalidToken")).thenReturn(null);
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);
        when(jwtUtil.validateToken("invalidToken", userDetails.getUsername())).thenReturn(false);

        jwtRequestFilter.doFilterInternal(request,response,chain);

        verify(userDetailsService,times(0)).loadUserByUsername("username");
        verify(chain,times(1)).doFilter(request,response);

        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    void testNoToken() throws IOException, ServletException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(jwtUtil.extractUsername("noToken")).thenReturn(null);
        when(userDetailsService.loadUserByUsername("noToken")).thenReturn(null);
        when(jwtUtil.validateToken("noToken", "noToken")).thenReturn(false);

        jwtRequestFilter.doFilterInternal(request,response,chain);

        verify(userDetailsService,times(0)).loadUserByUsername("noToken");
        verify(chain,times(1)).doFilter(request,response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
