package com.vitalink.backend.controller;

import com.vitalink.backend.service.ActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Agente 3: Activation endpoint — interfaz HTTP para activar cuenta y reenviar email
@RestController
@RequestMapping("/api/v1/auth")
public class ActivationController {

    private final ActivationService activationService;

    public ActivationController(ActivationService activationService) {
        this.activationService = activationService;
    }

    /**
     * GET /api/v1/auth/activate?token=xxx
     * El frontend redirige al usuario aquí cuando hace clic en el link del email.
     */
    @GetMapping("/activate")
    public ResponseEntity<Map<String, String>> activate(@RequestParam String token) {
        activationService.activateAccount(token);
        return ResponseEntity.ok(Map.of("message", "Cuenta activada correctamente"));
    }

    /**
     * POST /api/v1/auth/resend-activation?email=xxx
     * Reenvía el email de activación si el usuario no lo recibió.
     */
    @PostMapping("/resend-activation")
    public ResponseEntity<Map<String, String>> resendActivation(@RequestParam String email) {
        activationService.resendToken(email);
        return ResponseEntity.ok(Map.of("message", "Email de activación reenviado"));
    }
}
