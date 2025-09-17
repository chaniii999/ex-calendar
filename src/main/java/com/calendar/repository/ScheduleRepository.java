package com.calendar.repository;

import com.calendar.entity.Schedule;
import com.calendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    // 특정날짜 조회 // n일 조회
    List<Schedule> findByUserAndStartDate(User user, LocalDate startDate);
    // 특정기간 조회 // n일 ~ n일 조회
    List<Schedule> findByUserAndStartDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
