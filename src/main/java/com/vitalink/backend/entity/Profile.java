package com.vitalink.backend.entity;

import com.vitalink.backend.entity.converter.ContactListConverter;
import com.vitalink.backend.entity.converter.MedicalDataConverter;
import com.vitalink.backend.entity.converter.PersonalDataConverter;
import com.vitalink.backend.entity.converter.VisibilityConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountId;

    @Convert(converter = PersonalDataConverter.class)
    @Column(columnDefinition = "TEXT")
    private PersonalData personal;

    @Convert(converter = MedicalDataConverter.class)
    @Column(columnDefinition = "TEXT")
    private MedicalData medical;

    @Convert(converter = ContactListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Contact> contacts;

    @Convert(converter = VisibilityConverter.class)
    @Column(columnDefinition = "TEXT")
    private ProfileVisibility visibility;
}
