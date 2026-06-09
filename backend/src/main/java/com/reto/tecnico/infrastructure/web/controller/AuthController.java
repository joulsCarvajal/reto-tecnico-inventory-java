package com.reto.tecnico.infrastructure.web.controller;

import com.reto.tecnico.application.dto.request.LoginRequest;
import com.reto.tecnico.application.dto.response.ApiResponse;
import com.reto.tecnico.application.dto.response.AuthResponse;
import com.reto.tecnico.application.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }
}
