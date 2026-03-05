package com.vitalink.backend.controller;

import com.vitalink.backend.entity.Profile;
import com.vitalink.backend.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Profile> getByAccountId(@PathVariable String accountId, HttpServletRequest request) {
        return ResponseEntity.ok(profileService.getByAccountId(accountId));
    }

    @PostMapping
    public ResponseEntity<Profile> create(@RequestBody Profile profile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.create(profile));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Profile> update(@PathVariable String accountId, @RequestBody Profile profile) {
        return ResponseEntity.ok(profileService.update(accountId, profile));
    }
}
