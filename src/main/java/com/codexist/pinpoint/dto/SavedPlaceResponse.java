package com.codexist.pinpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SavedPlaceResponse {
    private String id;
    private String placeId;
    private String placeName;
    private String customName;
    private Double latitude;
    private Double longitude;
    private String address;
    private LocalDateTime createdAt;
}

