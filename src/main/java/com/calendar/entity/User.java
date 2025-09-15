package com.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Builder
@Getter
public class User {

    @Id
    @Column(length = 26, nullable = false, updatable = false)
    String id;

    @Column(length = 50, nullable = false, unique = true)
    String email;

    @Column(length = 50, nullable = false)
    String password;

    @Column(length = 20, nullable = false)
    String nickname;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    @Column(nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

}
