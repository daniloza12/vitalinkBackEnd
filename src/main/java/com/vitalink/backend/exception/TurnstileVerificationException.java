package com.vitalink.backend.exception;

public class TurnstileVerificationException extends RuntimeException {
    public TurnstileVerificationException() {
        super("Verificación de seguridad fallida");
    }
}
