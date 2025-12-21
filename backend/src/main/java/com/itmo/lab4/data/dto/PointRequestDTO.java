package com.itmo.lab4.data.dto;


// DTO for data submitted by the Angular client (X, Y, R)
public record PointRequestDTO(
        Double x,
        Double y,
        Double r,
        String source // "form" or "canvas"
) {}