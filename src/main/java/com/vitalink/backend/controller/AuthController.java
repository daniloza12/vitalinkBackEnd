package com.vitalink.backend.controller;

import com.vitalink.backend.dto.LoginRequest;
import com.vitalink.backend.dto.RegisterRequest;
import com.vitalink.backend.entity.Account;
import com.vitalink.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody RegisterRequest request) {
        Account account = authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody LoginRequest request) {
        Account account = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(account);
    }
}
