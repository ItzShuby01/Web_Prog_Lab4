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
    private LocalDateTime currentTime;
    private Long executionTime;
    private String username;

    // Constructor used when a new point is calculated
    public CalculationResultDTO(Double x, Double y, Double r, Boolean hit, LocalDateTime currentTime, Long executionTime, String username) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.currentTime = currentTime;
        this.executionTime = executionTime;
        this.username = username;
    }


    // no-args constructor for Spring/JSON mapping
    public CalculationResultDTO() {}
}