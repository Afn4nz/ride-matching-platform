package com.ridematch.driver.entity;

import com.ridematch.driver.enums.RegistrationType;
import com.ridematch.entity.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "registration_document_id")
  private Document registrationDocument;

  private String maker;

  private String model;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(
      name = "vehicle_images",
      joinColumns = @JoinColumn(name = "vehicle_id"),
      inverseJoinColumns = @JoinColumn(name = "document_id"))
  private List<Document> images;
}
