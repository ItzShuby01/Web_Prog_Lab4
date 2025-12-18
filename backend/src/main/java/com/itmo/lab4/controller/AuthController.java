package com.itmo.lab4.controller;

import com.itmo.lab4.data.dto.AuthRequest;
import com.itmo.lab4.data.entity.User;
import com.itmo.lab4.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
@RequestMapping("/api/auth") // Base URL for all auth endpoints
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    // Endpoint 1: POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
        try {
            User newUser = authService.registerUser(request);
            // Return 201 Created status
            return new ResponseEntity<>("User registered successfully: " + newUser.getUsername(), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            // Return 409 Conflict if username already exists
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            // Return 400 Bad Request for missing fields
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint 2: POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest request) {
        try {
            // Authenticate the user using the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // If successful, set the authentication object in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Successful authentication -> means the next protected request will carry the necessary credentials.
            return new ResponseEntity<>("User logged in successfully!", HttpStatus.OK);

        } catch (Exception e) {
            // Catch exceptions thrown by authenticationManager like BadCredentialsException
            return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED);
        }
    }
}