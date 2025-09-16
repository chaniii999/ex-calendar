package com.calendar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class User {

    @Id
    @Column(length = 26, nullable = false, updatable = false)
    private String id;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Setter
    @Column(length = 50, nullable = false)
    private String password;

    @Setter
    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

}
