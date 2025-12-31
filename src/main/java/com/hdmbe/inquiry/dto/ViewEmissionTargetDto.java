package com.hdmbe.inquiry.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ViewEmissionTargetDto {

    // 총 배출량 상단 카드 및 비교 막대 그래프
    private BigDecimal totalTarget;
    private BigDecimal totalActual;
    private Double totalAchievementRate;    // 달성율 or 증감률

    // 하단 월별 추이 그래프 (막대 + 꺾은선)
    private List<MonthlyComparisonDto> monthlyData;

    // 이번 연도의 최신 데이터가 몇 월까지인지 알려주는 필드
    private Integer latestMonth;

    @Data
    @Builder
    public static class MonthlyComparisonDto {
        private int month;
        private BigDecimal target;
        private BigDecimal actual;
    }
}
