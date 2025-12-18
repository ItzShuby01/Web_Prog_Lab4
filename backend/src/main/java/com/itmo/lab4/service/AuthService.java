package com.itmo.lab4.service;

import com.itmo.lab4.data.dto.AuthRequest;
import com.itmo.lab4.data.entity.User;
import com.itmo.lab4.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Handles user registration
    public User registerUser(AuthRequest request) throws DataIntegrityViolationException, IllegalArgumentException {
        // Simple check to ensure required fields are present
        if (request.getUsername() == null || request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Username and password must be provided.");
        }

        // Check if user already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            // A specific exception that the controller can map to a 409 Conflict status
            throw new DataIntegrityViolationException("User with this username already exists.");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());

        // HASH the password before saving to the database
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(newUser);
    }
}