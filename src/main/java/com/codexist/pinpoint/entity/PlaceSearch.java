package com.codexist.pinpoint.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "place_searches", indexes = {
        @Index(name = "idx_search_params", columnList = "latitude, longitude, radius")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer radius;

    @Column
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String response;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        //Cache for 24 hours
        expiresAt = LocalDateTime.now().plusHours(24);
    }
}