package com.hdmbe.inquiry.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplyCustomerInquiryResponseDto {

    private String customerName;

    private BigDecimal totalEmission;

    private BigDecimal ratio;

    private BigDecimal totalDistance;

    private Long tripCount;

    private BigDecimal avgEmission;

    private List<BigDecimal> monthlyTrend;
}
