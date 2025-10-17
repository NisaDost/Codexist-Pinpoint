package com.codexist.pinpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SavePlaceRequest {
    @NotBlank(message = "Place ID is required.")
    private String placeId;

    @NotBlank(message = "Place name is required.")
    private String placeName;

    @NotNull(message = "Latitude is required.")
    private Double latitude;

    @NotNull(message = "Longitude is required.")
    private Double longitude;

    private String type;

    private String address;

    private String customName;
}