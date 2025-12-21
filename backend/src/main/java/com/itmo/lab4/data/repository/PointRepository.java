package com.itmo.lab4.data.repository;


import com.itmo.lab4.data.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    // Custom query generated automatically by Spring Data JPA
    // Find all points from ALL users, ordered by ID desc (latest first)
    Page<Point> findAllByOrderByIdDesc(Pageable pageable);
    // For clearing history (Only current user's points)
    List<Point> findByUserId(Long userId);
}