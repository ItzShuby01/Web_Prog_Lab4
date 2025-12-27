package com.itmo.lab4.controller;

import com.itmo.lab4.data.dto.CalculationResultDTO;
import com.itmo.lab4.data.dto.PointRequestDTO;
import com.itmo.lab4.data.entity.Point;
import com.itmo.lab4.data.entity.User;
import com.itmo.lab4.data.repository.PointRepository;
import com.itmo.lab4.data.repository.UserRepository;
import com.itmo.lab4.service.AreaService;
import com.itmo.lab4.service.PointValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/area") // Base URL for the main application endpoints
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;
    private final PointRepository pointRepository;
    private final UserRepository userRepository; // To fetch the User object
    private final PointValidationService validationService;

    // Helper method to get the current authenticated User entity
    private User getCurrentUser(UserDetails userDetails) {
        // Since the user is authenticated -> they exist in the DB
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
    }

    // --- Endpoint 1: Submit a new point check
    @PostMapping("/check")
    public ResponseEntity<CalculationResultDTO> checkPoint(@RequestBody PointRequestDTO request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {

        // Backend Validation
        validationService.validate(request);

        // Get the authenticated User
        User currentUser = getCurrentUser(userDetails);

        // Delegate ALL business logic to AreaService
        CalculationResultDTO resultDTO = areaService.processAndSavePoint(request, currentUser);

        // Return the result DTO to the Angular client
        return new ResponseEntity<>(resultDTO, HttpStatus.CREATED);
    }

    // --- Endpoint 2: Get all results for all users (GET /api/area/history)
    // Now supports pagination and returns Page
    @GetMapping("/history")
    public ResponseEntity<Page<CalculationResultDTO>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Delegate business logic to the AreaService
        Page<CalculationResultDTO> dtoPage = areaService.getHistoryPage(page, size);

        return ResponseEntity.ok(dtoPage);
    }

    // --- Endpoint 3: Clear all results for the current user ( DELETE /api/area/history)
    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory(@AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getCurrentUser(userDetails);

        // Fetch all points for the user
        List<Point> userPoints = pointRepository.findByUserId(currentUser.getId());

        // Delete them (using deleteAll instead of delete one-by-one)
        pointRepository.deleteAll(userPoints);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content is standard for successful deletions
    }
}