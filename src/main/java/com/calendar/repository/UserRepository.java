package com.calendar.repository;

import com.calendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
