package com.codexist.pinpoint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SavePlaceRequest {
    @NotBlank(message = "Place ID is required.")
    private String placeId;

    @NotBlank(message = "Place name is required.")
    private String placeName;

    @NotBlank(message = "Latitude is required.")
    private Double latitude;

    @NotBlank(message = "Longitude is required.")
    private Double longitude;

    private String address;

    @NotBlank(message = "Custom name is required.") //nullable olabilir.
    private String customName;
}
