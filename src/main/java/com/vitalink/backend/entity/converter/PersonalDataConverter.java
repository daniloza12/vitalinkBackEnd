package com.vitalink.backend.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitalink.backend.entity.PersonalData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PersonalDataConverter implements AttributeConverter<PersonalData, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PersonalData attribute) {
        if (attribute == null) return null;
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PersonalData convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return mapper.readValue(dbData, PersonalData.class);
        } catch (Exception e) {
            return null;
        }
    }
}
