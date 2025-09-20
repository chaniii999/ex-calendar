package com.calendar.service;

import com.calendar.dto.schedule.ScheduleReq;
import com.calendar.dto.schedule.ScheduleResponse;
import com.calendar.entity.Schedule;
import com.calendar.entity.User;
import com.calendar.mapper.ScheduleMapper;
import com.calendar.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    // 스케줄 생성
    public ScheduleResponse create(ScheduleReq req, User user) {
        Schedule schedule = scheduleMapper.toEntity(req);
        schedule.setUser(user);
        scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(schedule);
    }

    // 스케줄 단건 조회
    public ScheduleResponse getById(String id, User user) {
        Schedule schedule = scheduleRepository.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 없거나 권한이 없습니다."));
        return scheduleMapper.toResponse(schedule);
    }

    // 특정 날짜 스케쥴 조회
    public List<ScheduleResponse> getByDate(LocalDate date, User user) {
        return scheduleRepository.findByUserAndStartDate(user, date).stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 특정 기간 스케쥴 조회
    public List<ScheduleResponse> getByPeriod(LocalDate startDate, LocalDate endDate, User user) {
        return scheduleRepository.findByUserAndStartDateBetween(user, startDate, endDate).stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 스케줄 수정
    public ScheduleResponse update(String id, ScheduleReq req, User user) {
        Schedule schedule = scheduleRepository.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 없거나 권한이 없습니다."));

        // 요청 데이터로 기존 스케줄 업데이트
        schedule.setTitle(req.getTitle());
        schedule.setDescription(req.getDescription());
        schedule.setColor(req.getColor());
        schedule.setStartDate(LocalDate.parse(req.getStartDate()));
        schedule.setStartAt(req.getStartAt() != null ? java.time.LocalDateTime.parse(req.getStartAt()) : null);
        schedule.setEndAt(req.getEndAt() != null ? java.time.LocalDateTime.parse(req.getEndAt()) : null);
        schedule.setAlarmEnabled(req.isAlarmEnabled());

        return scheduleMapper.toResponse(scheduleRepository.save(schedule));
    }

    public void delete(String id, User user) {
        Schedule schedule = scheduleRepository.findById(id)
                .filter(s -> s.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 없거나 권한이 없습니다."));
        scheduleRepository.delete(schedule);
    }

}
