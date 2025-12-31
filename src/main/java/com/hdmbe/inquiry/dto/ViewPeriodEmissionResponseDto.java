package com.hdmbe.inquiry.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ViewPeriodEmissionResponseDto {
    // 1. 선택한 기간
    private EmissionSummary current;

    // 2. 작년 동기간
    private EmissionSummary lastYear;

    @Data
    @Builder
    public static class EmissionSummary {

        private BigDecimal scope1;

        private BigDecimal scope3;

        private BigDecimal totalEmission;

        private BigDecimal totalDistance;
    }
}
