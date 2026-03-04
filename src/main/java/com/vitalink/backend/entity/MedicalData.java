package com.vitalink.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalData {
    private String diseases;
    private String allergies;
    private String others;
}
