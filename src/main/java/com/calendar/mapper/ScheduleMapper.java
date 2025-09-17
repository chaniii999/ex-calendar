package com.calendar.mapper;

import com.calendar.dto.schedule.ScheduleReq;
import com.calendar.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(ScheduleMapper.class);

    @Mapping(target = "id", ignore = true) // ULID는 @PrePersist에서 자동 생성
    @Mapping(target = "user", ignore = true) // Service 레벨에서 주입
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.parse(request.getStartDate()))")
    @Mapping(target = "startAt", expression = "java(request.getStartAt() != null ? java.time.LocalDateTime.parse(request.getStartAt()) : null)")
    @Mapping(target = "endAt", expression = "java(request.getEndAt() != null ? java.time.LocalDateTime.parse(request.getEndAt()) : null)")
    Schedule toEntity(ScheduleReq request);
}
