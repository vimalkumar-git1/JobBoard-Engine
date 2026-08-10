package com.vimalkumar.careerportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vimalkumar.careerportal.dto.AuthResponse;
import com.vimalkumar.careerportal.dto.LoginRequest;
import com.vimalkumar.careerportal.dto.RegisterRequest;
import com.vimalkumar.careerportal.service.AuthService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:5174", "http://127.0.0.1:5174", "http://[::1]:5174"})
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
