package com.calendar.mapper;

import com.calendar.dto.schedule.ScheduleReq;
import com.calendar.dto.schedule.ScheduleResponse;
import com.calendar.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(ScheduleMapper.class);

    @Mapping(target = "id", ignore = true) // ULID는 @PrePersist에서 자동 생성
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "color", source = "request.color")
    @Mapping(target = "startDate", expression =
            "java(java.time.LocalDate.parse(request.getStartDate()))")
    @Mapping(target = "startAt", expression =
            "java(request.getStartAt() != null ? java.time.LocalDateTime.parse(request.getStartAt()) : null)")
    @Mapping(target = "endAt", expression =
            "java(request.getEndAt() != null ? java.time.LocalDateTime.parse(request.getEndAt()) : null)")
    @Mapping(target = "allDay", ignore = true) // allDay 기본값 false
    @Mapping(target = "alarmEnabled", source = "request.alarmEnabled")
    @Mapping(target = "user", ignore = true) // Service 레벨에서 주입
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Schedule toEntity(ScheduleReq request);

    @Mapping(target = "startDate", expression = "java(entity.getStartDate().toString())")
    @Mapping(target = "startAt", expression = "java(entity.getStartAt() != null ? entity.getStartAt().toString() : null)")
    @Mapping(target = "endAt", expression = "java(entity.getEndAt() != null ? entity.getEndAt().toString() : null)")
    ScheduleResponse toResponse(Schedule entity);
}
