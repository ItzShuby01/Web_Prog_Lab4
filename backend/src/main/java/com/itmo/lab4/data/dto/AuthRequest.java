package com.itmo.lab4.data.dto;


// A simple DTO to hold the login/registration credentials sent from the client
public record AuthRequest (String username, String password){}