package com.vitalink.backend.service.impl;

import com.vitalink.backend.entity.Account;
import com.vitalink.backend.exception.ResourceNotFoundException;
import com.vitalink.backend.repository.AccountRepository;
import com.vitalink.backend.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account getById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    @Override
    public Account getBySecurityAccount(String securityAccount) {
        return accountRepository.findBySecurityAccount(securityAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with securityAccount: " + securityAccount));
    }

    @Override
    public Account update(String id, Account data) {
        Account existing = getById(id);

        if (data.getEmail() != null) existing.setEmail(data.getEmail());
        if (data.getRole() != null) existing.setRole(data.getRole());
        if (data.getQrDataUrl() != null) existing.setQrDataUrl(data.getQrDataUrl());
        if (data.getSecurityAccount() != null) existing.setSecurityAccount(data.getSecurityAccount());

        return accountRepository.save(existing);
    }

    @Override
    public Account updateStatus(String id, String status) {
        Account existing = getById(id);
        existing.setStatus(status);
        return accountRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        Account existing = getById(id);
        accountRepository.delete(existing);
    }
}
