package com.hdmbe.carbonEmission.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarbonEmissionFactorUpdateRequest {
    private BigDecimal emissionFactor;
    private String remark;
}