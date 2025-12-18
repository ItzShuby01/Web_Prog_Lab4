package com.itmo.lab4.data.dto;

import lombok.Data;

import java.time.LocalDateTime;

// Used for transferring data back to Angular
@Data
public class CalculationResultDTO {

    private Double x;
    private Double y;
    private Double r;
    private Boolean hit;
    private LocalDateTime executionTime;

    // Constructor used when a new point is calculated
    public CalculationResultDTO(Double x, Double y, Double r, Boolean hit, LocalDateTime executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.executionTime = executionTime;
    }

    // Constructor used when loading existing points (e.g from Point.java Entity)
    public CalculationResultDTO(Double x, Double y, Double r, Boolean hit, LocalDateTime executionTime, Long id) {
        this(x, y, r, hit, executionTime);
    }

    // no-args constructor for Spring/JSON mapping
    public CalculationResultDTO() {}
}