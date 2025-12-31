package com.hdmbe.inquiry.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeInquiryResponseDto {

    // 운행 목적명
    private String purposeName;

    // 총 탄소 배출량
    private BigDecimal totalEmission;

    // 전체 대비 비율 (%)
    private BigDecimal ratio;

    // 운행 거리 총합
    private BigDecimal totalDistance;

    // 운행 횟수
    private Long tripCount;

    // 평균 탄소 배출량 (총 배출량 / 운행 횟수)
    private BigDecimal avgEmission;

    // 월별 추이 (그래프용)
    private List<BigDecimal> monthlyTrend;
}
