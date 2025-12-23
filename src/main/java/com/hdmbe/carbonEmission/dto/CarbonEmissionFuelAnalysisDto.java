package com.hdmbe.carbonEmission.dto;

import com.hdmbe.commonModule.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonEmissionFuelAnalysisDto {

    // 조회
    private Integer year;

    private Integer month;

    private Integer defaultScope;

    private FuelType fuelType;

    // 응답
    // 프론트 key (line chart / pie chart)
    private String name;                // FuelType 이름

    // 집계 값
    private BigDecimal totalEmission;   // 총 배출량
    private BigDecimal ratio;           // 전체 대비 %

    // 월별 추이 (1~12월)
    private List<BigDecimal> monthlyTrend;

}
