package com.itmo.lab4.data.repository;


import com.itmo.lab4.data.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    // Custom query generated automatically by Spring Data JPA
    // Finds all points associated with a user ID, ordered by the newest first.
    List<Point> findByUserIdOrderByIdDesc(Long userId);
}