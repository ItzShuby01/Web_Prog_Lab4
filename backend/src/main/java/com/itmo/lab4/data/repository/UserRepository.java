package com.itmo.lab4.data.repository;

import com.itmo.lab4.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data automatically generates the implementation for this method.
    // Crucial for checking if a user exists during login/registration.
    Optional<User> findByUsername(String username);
}