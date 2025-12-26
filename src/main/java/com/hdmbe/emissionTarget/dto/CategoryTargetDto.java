package com.hdmbe.emissionTarget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTargetDto {

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    private List<MonthlyValueDto> monthly = new ArrayList<>();

    public static CategoryTargetDto empty() {
        List<MonthlyValueDto> defaults = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            defaults.add(MonthlyValueDto.builder().month(i).value(BigDecimal.ZERO).build());
        }
        return CategoryTargetDto.builder()
                .total(BigDecimal.ZERO)
                .monthly(defaults)
                .build();
    }
}
