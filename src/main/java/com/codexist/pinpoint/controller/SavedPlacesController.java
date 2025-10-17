package com.codexist.pinpoint.controller;

import com.codexist.pinpoint.dto.ErrorResponse;
import com.codexist.pinpoint.dto.SavePlaceRequest;
import com.codexist.pinpoint.dto.SavedPlaceResponse;
import com.codexist.pinpoint.entity.SavedPlace;
import com.codexist.pinpoint.service.SavedPlaceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/saved-places")
public class SavedPlacesController {

    @Autowired
    private SavedPlaceService savedPlaceService;

    @PostMapping
    public ResponseEntity<?> savePlace (
            @Valid @RequestBody SavePlaceRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        try{
            SavedPlaceResponse response = savedPlaceService.savePlace(request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (RuntimeException e){
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Save failed.",
                    e.getMessage(),
                    httpRequest.getRequestURI()
            );

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<SavedPlaceResponse>> getSavedPlaces(Authentication authentication) {
        List<SavedPlaceResponse> savedPlaces = savedPlaceService.getUserSaveDPlaceS(authentication.getName());
        return ResponseEntity.ok(savedPlaces);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavedPlace(
            @PathVariable String id,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {
        try {
            savedPlaceService.deleteSavedPlace(id, authentication.getName());
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e){
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Delete Failed",
                    e.getMessage(),
                    httpRequest.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
