package com.itmo.lab4.service;

import com.itmo.lab4.data.dto.PointRequestDTO;
import org.springframework.stereotype.Service;
import java.util.List;

// Validates INPUTS based on the source (Form / Canvas Click)
@Service
public class PointValidationService {

    private final List<Double> ALLOWED_X_VALUES_FORM = List.of(-3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0);
    private final List<Double> ALLOWED_R_VALUES = List.of(1.0, 2.0, 3.0, 4.0, 5.0);

    public void validate(PointRequestDTO request) {
        // Validate R (common for both sources)
        if (request.r() == null || !ALLOWED_R_VALUES.contains(request.r())) {
            throw new IllegalArgumentException("R must be positive and one of: 1, 2, 3, 4, 5");
        }

        // Source-specific validation
        if ("form".equalsIgnoreCase(request.source())) {
            validateForm(request);
        } else {
            validateCanvas(request);
        }
    }

    private void validateForm(PointRequestDTO req) {
        if (req.x() == null || !ALLOWED_X_VALUES_FORM.contains(req.x())) {
            throw new IllegalArgumentException("Form X must be a selected value from -3 to 5.");
        }
        if (req.y() == null || req.y() < -3 || req.y() > 3) {
            throw new IllegalArgumentException("Form Y must be between -3 and 3.");
        }
    }

    private void validateCanvas(PointRequestDTO req) {
        if (req.x() == null || req.x() < -5 || req.x() > 5) {
            throw new IllegalArgumentException("Canvas X must be between -5 and 5.");
        }
        if (req.y() == null || req.y() < -5 || req.y() > 5) {
            throw new IllegalArgumentException("Canvas Y must be between -5 and 5.");
        }
    }
}