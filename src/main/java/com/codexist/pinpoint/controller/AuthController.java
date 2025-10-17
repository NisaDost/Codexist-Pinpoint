package com.codexist.pinpoint.controller;


import com.codexist.pinpoint.dto.AuthResponse;
import com.codexist.pinpoint.dto.ErrorResponse;
import com.codexist.pinpoint.dto.LoginRequest;
import com.codexist.pinpoint.dto.RegisterRequest;
import com.codexist.pinpoint.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        try{
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        }
        catch(RuntimeException e){
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Register failed.",
                    e.getMessage(),
                    "/api/auth/register"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        try{
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        }
        catch(RuntimeException e){
            ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Login failed.",
                "Invalid username or password.",
                "/api/auth/login"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
