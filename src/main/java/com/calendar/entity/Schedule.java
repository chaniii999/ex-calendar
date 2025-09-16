package com.calendar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder @Getter
public class Schedule {
    @Id
    @Column(length = 26, nullable = false, updatable = false)
    private String id;

    @Setter
    @Column(length = 30, nullable = false)
    private String title;

    @Setter
    @Column(length = 500)
    private String description;

    @Setter
    @Column(nullable = false)
    private LocalDate startDate;

    @Setter
    private LocalDateTime startAt;

    @Setter
    private LocalDateTime endAt;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private boolean allDay = false;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private boolean alarmEnabled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

}
