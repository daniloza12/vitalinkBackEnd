package com.vitalink.backend.entity.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitalink.backend.entity.MedicalData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MedicalDataConverter implements AttributeConverter<MedicalData, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(MedicalData attribute) {
        if (attribute == null) return null;
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public MedicalData convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return mapper.readValue(dbData, MedicalData.class);
        } catch (Exception e) {
            return null;
        }
    }
}
