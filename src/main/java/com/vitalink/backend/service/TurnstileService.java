package com.vitalink.backend.service;

public interface TurnstileService {
    void verify(String cfToken);
}
