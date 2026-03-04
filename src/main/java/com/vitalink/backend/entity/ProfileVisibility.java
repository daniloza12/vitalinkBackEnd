package com.vitalink.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVisibility {

    private PersonalVisibility personal;
    private List<ContactVisibility> contacts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalVisibility {
        private Boolean sex;
        private Boolean birthDate;
        private Boolean fullName;
        private Boolean phone;
        private Boolean personalEmail;
        private Boolean workEmail;
        private Boolean city;
        private Boolean district;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactVisibility {
        private Boolean fullName;
        private Boolean phone;
        private Boolean personalEmail;
        private Boolean workEmail;
    }
}
