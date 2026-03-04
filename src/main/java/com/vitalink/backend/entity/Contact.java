package com.vitalink.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    private String fullName;
    private String phone;
    private String personalEmail;
    private String workEmail;
}
