package com.itmo.lab4.controller;

import com.itmo.lab4.data.dto.CalculationResultDTO;
import com.itmo.lab4.data.dto.PointRequestDTO;
import com.itmo.lab4.service.AreaService;
import com.itmo.lab4.service.PointValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointValidationService validationService;
    private final AreaService areaService;
    @PostMapping
    public ResponseEntity<?> addPoint(@RequestBody PointRequestDTO request) {

        // Validate based on source (form vs canvas)
        validationService.validate(request);

        // Process the point using AreaService
        CalculationResultDTO result = areaService.checkPoint(
                request.x(),
                request.y(),
                request.r()
        );

        return ResponseEntity.ok(result);
    }
}