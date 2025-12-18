package com.itmo.lab4.controller;

import com.itmo.lab4.data.dto.CalculationResultDTO;
import com.itmo.lab4.data.dto.PointRequestDTO;
import com.itmo.lab4.data.entity.Point;
import com.itmo.lab4.data.entity.User;
import com.itmo.lab4.data.repository.PointRepository;
import com.itmo.lab4.data.repository.UserRepository;
import com.itmo.lab4.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
@RequestMapping("/api/area") // Base URL for the main application endpoints
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;
    private final PointRepository pointRepository;
    private final UserRepository userRepository; // To fetch the User object

    // Helper method to get the current authenticated User entity
    private User getCurrentUser(UserDetails userDetails) {
        // Since the user is authenticated -> they exist in the DB
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
    }

    //  --- Endpoint 1: Submit a new point check ( POST /api/area/check)
    @PostMapping("/check")
    public ResponseEntity<CalculationResultDTO> checkPoint(@RequestBody PointRequestDTO request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {

        // Get the authenticated User
        User currentUser = getCurrentUser(userDetails);

        // Perform a simplified Validation (Front-end handles most validation)
        if (request.getX() == null || request.getY() == null || request.getR() == null || request.getR() <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Perform Calculation
        CalculationResultDTO resultDTO = areaService.checkPoint(
                request.getX(),
                request.getY(),
                request.getR()
        );

        // Convert DTO to Entity and Save to Database
        Point newPoint = areaService.toEntity(resultDTO);
        newPoint.setUser(currentUser); // Link the point to the current user !!
        pointRepository.save(newPoint);

        // Return the result DTO to the Angular client
        return new ResponseEntity<>(resultDTO, HttpStatus.CREATED);
    }

    // --- Endpoint 2: Get all results for the current user (GET /api/area/history)
    @GetMapping("/history")
    public ResponseEntity<List<CalculationResultDTO>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getCurrentUser(userDetails);

        // findAll by user ID
        List<Point> userPoints = pointRepository.findByUserIdOrderByIdDesc(currentUser.getId());

        // Convert the list of JPA Entities back to DTOs for the client
        List<CalculationResultDTO> dtos = userPoints.stream()
                .map(areaService::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // --- Endpoint 3: Clear all results for the current user ( DELETE /api/area/history)
    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory(@AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getCurrentUser(userDetails);

        // Fetch all points for the user
        List<Point> userPoints = pointRepository.findByUserIdOrderByIdDesc(currentUser.getId());

        // Delete them (using deleteAll instead of delete one-by-one)
        pointRepository.deleteAll(userPoints);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content is standard for successful deletions
    }
}