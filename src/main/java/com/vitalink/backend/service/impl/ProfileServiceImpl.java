package com.vitalink.backend.service.impl;

import com.vitalink.backend.entity.Profile;
import com.vitalink.backend.exception.ConflictException;
import com.vitalink.backend.exception.ResourceNotFoundException;
import com.vitalink.backend.repository.ProfileRepository;
import com.vitalink.backend.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile getByAccountId(String accountId) {
        return profileRepository.findByAccountId(accountId)
//                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for accountId: " + accountId));
                .orElse(null);
    }

    @Override
    public Profile create(Profile profile) {
        if (profileRepository.findByAccountId(profile.getAccountId()).isPresent()) {
            throw new ConflictException("Profile already exists for accountId: " + profile.getAccountId());
        }
        return profileRepository.save(profile);
    }

    @Override
    public Profile update(String accountId, Profile profile) {

        Optional<Profile> profileData = profileRepository.findByAccountId(profile.getAccountId());
        Profile newProfile = null;

        if (!profileData.isPresent()) {

            newProfile = new Profile();
            newProfile.setAccountId(accountId);

            if (profile.getPersonal() != null) newProfile.setPersonal(profile.getPersonal());
            if (profile.getMedical() != null) newProfile.setMedical(profile.getMedical());
            if (profile.getContacts() != null) newProfile.setContacts(profile.getContacts());
            if (profile.getVisibility() != null) newProfile.setVisibility(profile.getVisibility());
        }
        else {

            newProfile = profileData.get();

            if (profile.getPersonal() != null) newProfile.setPersonal(profile.getPersonal());
            if (profile.getMedical() != null) newProfile.setMedical(profile.getMedical());
            if (profile.getContacts() != null) newProfile.setContacts(profile.getContacts());
            if (profile.getVisibility() != null) newProfile.setVisibility(profile.getVisibility());

        }

        return profileRepository.save(newProfile);
    }
}
