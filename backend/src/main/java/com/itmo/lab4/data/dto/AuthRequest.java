package com.itmo.lab4.data.dto;

import lombok.Data;

// A simple DTO to hold the login/registration credentials sent from the client
@Data
public class AuthRequest {
    private String username;
    private String password;
}