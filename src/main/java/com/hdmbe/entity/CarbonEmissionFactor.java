package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CARBON_EMISSION_FACTOR")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonEmissionFactor extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 계수 ID
    @Column(name = "factor_id")
    private Long id;

    // 연료종류
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    // 배출 계수
    @Column(name = "emission_factor", precision = 6, scale = 2, nullable = false)
    private BigDecimal emissionFactor;

    // 배출 계수 단위 타입
    @Column(name = "unit_type", length = 10, nullable = false)
    private String unitType;
}
