package com.vitalink.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "rate_limit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, nullable = false)
    private String ip;

    @Column(length = 100, nullable = false)
    private String endpoint;

    @Column
    private LocalDateTime blockedUntil;
}
