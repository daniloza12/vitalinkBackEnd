package com.vitalink.backend.repository;

import com.vitalink.backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByAccountId(String accountId);
}
