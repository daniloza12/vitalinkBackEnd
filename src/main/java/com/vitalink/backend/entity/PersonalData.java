package com.vitalink.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalData {
    private String sex;
    private String birthDate;
    private String fullName;
    private String phone;
    private String personalEmail;
    private String workEmail;
    private String city;
    private String district;
}
