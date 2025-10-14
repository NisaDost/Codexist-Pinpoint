package com.codexist.pinpoint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SavePlaceRequest {
    @NotBlank(message = "Place ID is required.")
    private String placeId;

    @NotBlank(message = "Place name is required.")
    private String placeName; //silebilirim?

    @NotBlank(message = "Latitude is required.")
    private String latitude;

    @NotBlank(message = "Longitude is required.")
    private String longitude;

    private String address;

    @NotBlank(message = "Custom name is required.")
    private String customName;
}
