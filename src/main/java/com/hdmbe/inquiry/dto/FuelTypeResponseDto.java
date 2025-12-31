package com.hdmbe.inquiry.dto;

import com.hdmbe.commonModule.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelTypeResponseDto {

    // 연료
    private FuelType fuelType;
    // 총 배출량
    private BigDecimal totalEmission;
    // 전체 대비 비율 (%)
    private BigDecimal ratio;
    // 월별 추이 (그래프용)
    private List<BigDecimal> monthlyTrend;
}
