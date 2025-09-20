package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import com.calendar.dto.schedule.ScheduleReq;
import com.calendar.dto.schedule.ScheduleResponse;
import com.calendar.entity.User;
import com.calendar.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ApiResponse<ScheduleResponse> create(@RequestBody ScheduleReq req,
                                                @AuthenticationPrincipal User user) {
        return ApiResponse.of(true, "Schedule created successfully",
                scheduleService.create(req, user));
    }

    @GetMapping("/{id}")
    public ApiResponse<ScheduleResponse> getById(@PathVariable String id,
                                                @AuthenticationPrincipal User user) {
        return ApiResponse.of(true, "Schedule retrieved successfully",
                scheduleService.getById(id, user));
    }

    @GetMapping("/date/{date}")
    public ApiResponse<List<ScheduleResponse>> getByDate(@PathVariable String date,
                                                         @AuthenticationPrincipal User user) {

        return ApiResponse.of(true, "Schedules for the date retrieved successfully",
                scheduleService.getByDate(LocalDate.parse(date), user));
    }

    // 일정 특정 기간 조회
    @GetMapping("/period")
    public ApiResponse<List<ScheduleResponse>> getByPeriod(@RequestParam String startDate,
                                                           @RequestParam String endDate,
                                                           @AuthenticationPrincipal User user) {
        return ApiResponse.of(true, "Schedules for the period retrieved successfully",
                scheduleService.getByPeriod(LocalDate.parse(startDate), LocalDate.parse(endDate), user));
    }

    // 일정 업데이트
    @PutMapping("/{id}")
    public ApiResponse<ScheduleResponse> update(@PathVariable String id,
                                                @RequestBody ScheduleReq req,
                                                @AuthenticationPrincipal User user) {
        return ApiResponse.of(true, "Schedule updated successfully",
                scheduleService.update(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id,
                                      @AuthenticationPrincipal User user) {
        scheduleService.delete(id, user);
        return ApiResponse.of(true, "Schedule deleted successfully", null);
    }











}
