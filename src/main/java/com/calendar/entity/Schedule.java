package com.calendar.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder @Getter
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UlidCreator.getUlid().toString();
        }
    }

}
