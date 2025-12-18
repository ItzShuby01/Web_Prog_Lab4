package com.itmo.lab4.service;

import com.itmo.lab4.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor // Lombok generates a constructor for all final fields
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user in the database
        com.itmo.lab4.data.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert User entity to the Spring Security UserDetails object
        // Fetch the HASHED password from the DB.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Spring Security checks this against the submitted password
                new ArrayList<>() // Empty list for authorities/roles
        );
    }
}