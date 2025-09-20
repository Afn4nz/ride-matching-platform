package com.ridematch.ride.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Embeddable
public class Location {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
