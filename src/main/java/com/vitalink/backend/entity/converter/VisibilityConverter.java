package com.vitalink.backend.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitalink.backend.entity.ProfileVisibility;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VisibilityConverter implements AttributeConverter<ProfileVisibility, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ProfileVisibility attribute) {
        if (attribute == null) return null;
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ProfileVisibility convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return mapper.readValue(dbData, ProfileVisibility.class);
        } catch (Exception e) {
            return null;
        }
    }
}
