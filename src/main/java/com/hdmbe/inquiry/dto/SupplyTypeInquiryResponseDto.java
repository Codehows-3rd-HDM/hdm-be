package com.hdmbe.inquiry.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyTypeInquiryResponseDto {

    private String supplyTypeName;

    private BigDecimal totalEmission;

    private BigDecimal ratio;

    private BigDecimal totalDistance;

    private Long tripCount;

    private BigDecimal avgEmission;

    private List<BigDecimal> monthlyTrend;
}
