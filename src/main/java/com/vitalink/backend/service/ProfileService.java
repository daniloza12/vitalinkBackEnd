package com.vitalink.backend.service;

import com.vitalink.backend.entity.Profile;

public interface ProfileService {
    Profile getByAccountId(String accountId);
    Profile create(Profile profile);
    Profile update(String accountId, Profile profile);
}
