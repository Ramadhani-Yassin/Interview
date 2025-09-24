package com.interview.WebApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "uploads")
public class Upload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String fileName;

    @Column(nullable = false)
    private OffsetDateTime uploadedAt = OffsetDateTime.now();

    @Column(nullable = false)
    private Integer numberOfRows;

    @Column(nullable = false)
    private Integer numberOfColumns;

    // Comma-separated header names preserving order
    @Column(nullable = false, columnDefinition = "TEXT")
    private String headerLine;
} 