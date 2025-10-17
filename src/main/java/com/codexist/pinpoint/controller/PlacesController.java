package com.codexist.pinpoint.controller;

import com.codexist.pinpoint.dto.ErrorResponse;
import com.codexist.pinpoint.service.PlacesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/places")
public class PlacesController {

    @Autowired
    private PlacesService placesService;

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Integer radius,
            HttpServletRequest request,
            Authentication authentication
    ) {
        if (latitude < -90 || latitude > 90) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid parameter",
                            "Latitude must be between -90 and 90.",
                            request.getRequestURI()
                    )
            );
        }

        if (longitude < -180 || longitude > 180) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid parameter",
                            "Longitude must be between -180 and 180.",
                            request.getRequestURI()
                    )
            );
        }

        if (radius < 1 || radius > 50000) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid parameter",
                            "Radius must be between 1 and 50000 meters.",
                            request.getRequestURI()
                    )
            );
        }

        try {
            String userIdentifier = authentication != null && authentication.isAuthenticated()
                    ? authentication.getName()
                    : request.getRemoteAddr();

            String response = placesService.searchNearbyPlaces(latitude, longitude, radius, userIdentifier);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (RuntimeException e){
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Rate limit exceeded.",
                    e.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
        }
    }
}