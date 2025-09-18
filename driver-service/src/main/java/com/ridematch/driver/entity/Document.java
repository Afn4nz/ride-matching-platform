package com.ridematch.driver.entity;

import com.ridematch.driver.enums.DocumentType;
import com.ridematch.entity.AuditEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table
@Entity
public class Document extends AuditEntity {
  private DocumentType documentType;
  private String documentPath;
}
