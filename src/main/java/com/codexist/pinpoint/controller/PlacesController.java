package com.codexist.pinpoint.controller;

import com.codexist.pinpoint.service.PlacesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/places")
public class PlacesController {

    @Autowired
    private PlacesService placesService;

    @GetMapping("/nearby")
    public ResponseEntity<String> getNearbyPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Integer radius,
            @RequestParam String type,
            HttpServletRequest request,
            Authentication authentication
    ) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }

        if (radius < 1 || radius > 50000) {
            throw new IllegalArgumentException("Radius must be between 1 and 50000 meters.");
        }

        String userIdentifier = authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : request.getRemoteAddr();

        String response = placesService.searchNearbyPlaces(latitude, longitude, radius, type, userIdentifier);
        return ResponseEntity.ok(response);
    }
}