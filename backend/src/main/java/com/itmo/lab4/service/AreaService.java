package com.itmo.lab4.service;

import com.itmo.lab4.data.dto.CalculationResultDTO;
import com.itmo.lab4.data.dto.PointRequestDTO;
import com.itmo.lab4.data.entity.Point;
import com.itmo.lab4.data.entity.User;
import com.itmo.lab4.data.repository.PointRepository;
import lombok.RequiredArgsConstructor; // for dependency injection
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // for saving
import java.time.LocalDateTime;

@Service // Tells Spring this class holds business logic
@RequiredArgsConstructor // Needed to inject PointRepository
public class AreaService {

    private final PointRepository pointRepository;

    // Business logic only lives inside  service and not controller
    @Transactional
    public CalculationResultDTO processAndSavePoint(PointRequestDTO request, User user) {
        // Perform Calculation
        CalculationResultDTO resultDTO = checkPoint(
                request.x(),
                request.y(),
                request.r()
        );

        // Convert DTO to Entity and Save to Database
        Point newPoint = toEntity(resultDTO);
        newPoint.setUser(user); // Link the point to the current user
        pointRepository.save(newPoint);

        return resultDTO;
    }

    // Method to check a point & create the result object
    public CalculationResultDTO checkPoint(Double x, Double y, Double r) {
        if (r <= 0) {
            throw new IllegalArgumentException("Radius R must be positive.");
        }

        // Core Logic Calculation
        boolean hit = isHit(x, y, r);

        // DTO for response
        return new CalculationResultDTO(
                x,
                y,
                r,
                hit,
                LocalDateTime.now()
        );
    }

    public boolean isHit(double x, double y, double r) {
        if (r <= 0) return false;

        // Quadrant 1: Rectangle
        if (x >= 0 && x <= r / 2 && y >= 0 && y <= r) {
            return true;
        }

        // Quadrant 2: Triangle
        // Line equation: y - 0 = ((r/2 - 0) / (0 - (-r/2))) * (x - (-r/2))
        // => y = x + r/2
        if (x <= 0 && y >= 0 && y <= (x + r / 2)) {
            return true;
        }

        // Quadrant 3: Empty
        if (x <= 0 && y <= 0) {
            return false;
        }

        // Quadrant 4: Quarter Circle
        if (x >= 0 && y <= 0 && (x * x + y * y <= (r / 2) * (r / 2))) {
            return true;
        }

        return false;
    }

    // Utility method to convert DTO to JPA Entity (for saving to DB)
    public Point toEntity(CalculationResultDTO dto) {
        Point entity = new Point();
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setR(dto.getR());
        entity.setHit(dto.getHit());
        entity.setExecutionTime(dto.getExecutionTime());
        return entity;
    }

    // Utility method to convert JPA Entity to DTO (for sending to Front-end)
    public CalculationResultDTO toDTO(Point entity) {
        return new CalculationResultDTO(
                entity.getX(),
                entity.getY(),
                entity.getR(),
                entity.getHit(),
                entity.getExecutionTime(),
                entity.getId()
        );
    }
}