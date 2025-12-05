package com.hdmbe.component;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class EmissionCalculator {

    //탄소 배출량 계산 공식
    public BigDecimal calculate (BigDecimal distance, BigDecimal efficiency, BigDecimal factor)
    {
        if (efficiency.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;      // 0으로 나누기 방지

        // (거리 / 연비) x 배출계수
        return distance.divide(efficiency, 4, RoundingMode.HALF_UP).multiply(factor);
    }
}
