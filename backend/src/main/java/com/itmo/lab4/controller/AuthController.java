package com.itmo.lab4.controller;

import com.itmo.lab4.data.dto.AuthRequest;
import com.itmo.lab4.data.entity.User;
import com.itmo.lab4.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Base URL for all auth endpoints
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    // Endpoint 1: POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
            User newUser = authService.registerUser(request);
            // Return 201 Created status
            return new ResponseEntity<>("User registered successfully: " + newUser.getUsername(), HttpStatus.CREATED);
    }

    // Endpoint 2: POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest request) {
            // Authenticate the user using the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            // If successful, set the authentication object in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Successful authentication -> means the next protected request will carry the necessary credentials.
            return new ResponseEntity<>("User logged in successfully!", HttpStatus.OK);

    }
}