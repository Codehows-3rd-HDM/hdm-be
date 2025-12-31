package com.hdmbe.inquiry.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardYearlyDto {

    private int year;
    private BigDecimal scope1Actual;
    private BigDecimal scope3Actual;
    private BigDecimal scope1Target;
    private BigDecimal scope3Target;
    private BigDecimal totalTarget;
}
