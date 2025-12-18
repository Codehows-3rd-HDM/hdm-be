package com.hdmbe.carbonEmission.dto;

import com.hdmbe.carbonEmission.entity.CarbonEmissionFactor;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonEmissionFactorResponse {

    private Long id;
    private String fuelType;
    private String unitType;
    private BigDecimal emissionFactor;
    private String remark;

    public static CarbonEmissionFactorResponse fromEntity(CarbonEmissionFactor e) {
        return CarbonEmissionFactorResponse.builder()
                .id(e.getId())
                .fuelType(e.getFuelType().name())
                .emissionFactor(e.getEmissionFactor())
                .unitType(e.getUnitType())
                .remark(e.getRemark())
                .build();
    }
}