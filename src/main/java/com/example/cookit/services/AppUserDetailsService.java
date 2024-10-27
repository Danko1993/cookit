package com.example.cookit.services;
import com.example.cookit.entities.AppUser;
import com.example.cookit.exceptions.UserNotActivatedException;
import com.example.cookit.repositories.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!appUser.isEnabled()){
            throw new UserNotActivatedException("User with Username:" + username +
                    "has not activated account");
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(appUser.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), appUser.isEnabled(), true, true, true, authorities);
    }
}