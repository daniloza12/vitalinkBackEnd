package com.vitalink.backend.dto;

import com.vitalink.backend.entity.Account;

public class AuthResponse {

    private String token;
    private Account account;

    public AuthResponse(String token, Account account) {
        this.token = token;
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
