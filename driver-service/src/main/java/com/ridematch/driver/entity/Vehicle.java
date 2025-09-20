package com.ridematch.driver.entity;

import com.ridematch.driver.enums.RegistrationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class Vehicle extends AuditEntity {

    @Size(max = 1)
    @Column(name = "plate_letter1", length = 1)
    private String plateLetter1;

    @Size(max = 1)
    @Column(name = "plate_letter2", length = 1)
    private String plateLetter2;

    @Size(max = 1)
    @Column(name = "plate_letter3", length = 1)
    private String plateLetter3;

    private String plateNumber;

    @Enumerated(EnumType.STRING)
    private RegistrationType registrationType = RegistrationType.PRIVATE;

    private String registrationDocument;

    private String maker;

    private String model;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> images = new ArrayList<>();
}
