package com.vitalink.backend.controller;

import com.vitalink.backend.config.JwtUtil;
import com.vitalink.backend.dto.AuthResponse;
import com.vitalink.backend.dto.LoginRequest;
import com.vitalink.backend.dto.RegisterRequest;
import com.vitalink.backend.dto.ResetPasswordRequest;
import com.vitalink.backend.entity.Account;
import com.vitalink.backend.service.AuthService;
import com.vitalink.backend.service.PasswordResetService;
import com.vitalink.backend.service.TurnstileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final JwtUtil jwtUtil;
    private final TurnstileService turnstileService;

    public AuthController(AuthService authService,
                          PasswordResetService passwordResetService,
                          JwtUtil jwtUtil,
                          TurnstileService turnstileService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
        this.jwtUtil = jwtUtil;
        this.turnstileService = turnstileService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody RegisterRequest request) {
        turnstileService.verify(request.getCfToken());
        Account account = authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        turnstileService.verify(request.getCfToken());
        Account account = authService.login(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(account.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, account));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        passwordResetService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", "Si el correo existe, recibirás un enlace."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String token,
                                                             @RequestBody ResetPasswordRequest body) {
        passwordResetService.resetPassword(token, body.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
    }
}
