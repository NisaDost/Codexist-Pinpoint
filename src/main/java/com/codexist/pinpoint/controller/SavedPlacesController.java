package com.codexist.pinpoint.controller;

import com.codexist.pinpoint.dto.SavePlaceRequest;
import com.codexist.pinpoint.dto.SavedPlaceResponse;
import com.codexist.pinpoint.service.SavedPlaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-places")
public class SavedPlacesController {

    @Autowired
    private SavedPlaceService savedPlaceService;

    @PostMapping
    public ResponseEntity<SavedPlaceResponse> savePlace(
            @Valid @RequestBody SavePlaceRequest request,
            Authentication authentication) {
        SavedPlaceResponse response = savedPlaceService.savePlace(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SavedPlaceResponse>> getSavedPlaces(Authentication authentication) {
        List<SavedPlaceResponse> savedPlaces = savedPlaceService.getUserSavedPlaces(authentication.getName());
        return ResponseEntity.ok(savedPlaces);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavedPlace(
            @PathVariable String id,
            Authentication authentication
    ) {
        savedPlaceService.deleteSavedPlace(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}