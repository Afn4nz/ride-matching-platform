package com.ridematch.driver.entity;

import com.ridematch.driver.enums.DriverStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
public class Driver extends AuditEntity {

    private String fullName;

    private String phoneNumber;

    @Email private String email;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    private LocalDate licenseExpiryDate;

    private String licenseDocument;

    @Column(name = "rating_average", precision = 2, scale = 1)
    private BigDecimal ratingAverage;

    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    private Vehicle vehicle;

    private boolean isVerified;

    private boolean isAvailable;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    private Long currentRideId;
}
