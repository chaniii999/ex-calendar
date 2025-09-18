package com.calendar.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleReq {
    private String title;
    private String description;
    private String color;
    private String startDate; // "yyyy-MM-dd"
    private String startAt;   // "yyyy-MM-dd'T'HH:mm"
    private String endAt;     // "yyyy-MM-dd'T'HH:mm"

    private boolean alarmEnabled;

}
