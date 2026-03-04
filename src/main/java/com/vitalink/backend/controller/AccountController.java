package com.vitalink.backend.controller;

import com.vitalink.backend.entity.Account;
import com.vitalink.backend.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        return ResponseEntity.ok(accountService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable String id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @GetMapping("/security/{securityAccount}")
    public ResponseEntity<Account> getBySecurityAccount(@PathVariable String securityAccount) {
        return ResponseEntity.ok(accountService.getBySecurityAccount(securityAccount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable String id, @RequestBody Account data) {
        return ResponseEntity.ok(accountService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
