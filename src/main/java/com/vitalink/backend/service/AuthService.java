package com.vitalink.backend.service;

import com.vitalink.backend.entity.Account;

public interface AuthService {
    Account register(String email, String password);
    Account login(String email, String password);
}
