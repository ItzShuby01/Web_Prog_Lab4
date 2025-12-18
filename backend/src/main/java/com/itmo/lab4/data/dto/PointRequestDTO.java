package com.itmo.lab4.data.dto;

import lombok.Data;

// DTO for data submitted by the Angular client (X, Y, R)
@Data
public class PointRequestDTO {
    private Double x;
    private Double y;
    private Double r;
}