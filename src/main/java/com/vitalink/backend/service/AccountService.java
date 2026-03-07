package com.vitalink.backend.service;

import com.vitalink.backend.entity.Account;

import java.util.List;

public interface AccountService {
    List<Account> getAll();
    Account getById(String id);
    Account getBySecurityAccount(String securityAccount);
    Account update(String id, Account data);
    Account updateStatus(String id, String status);
    void delete(String id);
}
